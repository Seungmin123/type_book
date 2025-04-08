package com.muzlive.kitpage.kitpage.service.page;

import com.luciad.imageio.webp.WebPWriteParam;
import com.muzlive.kitpage.kitpage.domain.user.Image;
import com.muzlive.kitpage.kitpage.domain.user.repository.ImageRepository;
import com.muzlive.kitpage.kitpage.service.aws.s3.S3Service;
import com.muzlive.kitpage.kitpage.utils.constants.ApplicationConstants;
import com.muzlive.kitpage.kitpage.utils.enums.ImageCode;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.UUID;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@Service
public class FileService {

	private final S3Service s3Service;

	private final ImageRepository imageRepository;

	@Transactional
	public Long uploadConvertFile(String contentId, MultipartFile multipartFile, ImageCode code) throws Exception {

		// S3 Cover Image Upload
		String ext = "." + FilenameUtils.getExtension(multipartFile.getOriginalFilename());

		String saveFileName = UUID.randomUUID() + ".webp";
		String imagePath = contentId + "/" + ApplicationConstants.CONVERT + "/" + saveFileName;

		// 원본
		String originSaveFileName = saveFileName.replace(".webp", ext);
		String originImagePath = contentId + "/" + ApplicationConstants.IMAGE + "/" + originSaveFileName;
		s3Service.uploadFile(originImagePath, multipartFile);

		// Image DB Insert
		Image image = Image.of(imagePath, code, multipartFile);
		image.setSaveFileName(saveFileName);

		// Converting
		try {
			byte[] convertFile = this.convertToWebP(multipartFile.getBytes(), 1200, 0.8f);
			s3Service.uploadFile(imagePath, convertFile);
			BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(convertFile));
			image.setWidth(bufferedImage.getWidth());
			image.setHeight(bufferedImage.getHeight());
			image.setImageSize((long) convertFile.length);
			image.setMd5(DigestUtils.md5Hex(convertFile));

		} catch (Exception e) {
			log.error(e.getMessage());
		}

		return imageRepository.save(image).getImageUid();
	}

	private byte[] convertToWebP(byte[] file, int targetWidth, float quality) throws Exception {
		// MultipartFile에서 이미지를 읽어 BufferedImage로 변환
		BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(file));

		// 비율에 맞춰 세로 크기 계산
		int targetHeight = (int) ((double) targetWidth / originalImage.getWidth() * originalImage.getHeight());

		// 리사이즈된 이미지 생성
		java.awt.Image resizedImage = originalImage.getScaledInstance(targetWidth, targetHeight, java.awt.Image.SCALE_SMOOTH);
		BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);

		Graphics2D g2d = outputImage.createGraphics();
		g2d.drawImage(resizedImage, 0, 0, null);
		g2d.dispose();

		// WebP 포맷으로 변환하고 결과를 바이트 배열로 저장
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageWriter writer = ImageIO.getImageWritersByFormatName("webp").next();

		try (MemoryCacheImageOutputStream ios = new MemoryCacheImageOutputStream(baos)) {
			writer.setOutput(ios);

			WebPWriteParam writeParam = new WebPWriteParam(writer.getLocale());
			writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			writeParam.setCompressionType("Lossy"); // 압축 유형 설정
			writeParam.setCompressionQuality(quality); // 압축 품질 설정 (0.0 ~ 1.0)

			writer.write(null, new IIOImage(outputImage, null, null), writeParam);
		} catch (Exception e) {
			throw e;
		} finally {
			writer.dispose();
		}

		return baos.toByteArray();
	}

}
