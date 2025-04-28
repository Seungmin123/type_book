package com.muzlive.kitpage.kitpage.service.page;

import com.muzlive.kitpage.kitpage.domain.page.photobook.Pdf;
import com.muzlive.kitpage.kitpage.domain.page.photobook.repository.PdfRepository;
import com.muzlive.kitpage.kitpage.domain.user.Image;
import com.muzlive.kitpage.kitpage.domain.user.repository.ImageRepository;
import com.muzlive.kitpage.kitpage.service.FfmpegConverter;
import com.muzlive.kitpage.kitpage.service.aws.s3.S3Service;
import com.muzlive.kitpage.kitpage.utils.constants.ApplicationConstants;
import com.muzlive.kitpage.kitpage.utils.enums.ImageCode;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.UUID;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@Service
public class FileService {

	private final S3Service s3Service;

	private final FfmpegConverter ffmpegConverter;

	private final ImageRepository imageRepository;

	private final PdfRepository pdfRepository;

	@Transactional
	public Long uploadConvertImageFile(String contentId, MultipartFile multipartFile, ImageCode code) throws Exception {
		// TODO 결합도 지림 수정 필요

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
			//byte[] convertFile = this.convertToWebP(multipartFile.getBytes(), 1200, 0.8f);
			byte[] convertFile = ffmpegConverter.convertToWebp(multipartFile.getBytes(), ext);
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

	public Long convertImageBytesToPdf(String contentId, MultipartFile multipartFile, float jpegQuality) throws Exception {
		BufferedImage processedImage = ImageIO.read(new ByteArrayInputStream(multipartFile.getBytes()));

		// JPEG 압축
		ByteArrayOutputStream jpegOutput = new ByteArrayOutputStream();
		ImageWriter jpgWriter = ImageIO.getImageWritersByFormatName("jpg").next();
		ImageWriteParam jpgWriteParam = jpgWriter.getDefaultWriteParam();
		jpgWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		jpgWriteParam.setCompressionQuality(jpegQuality); // 0.0 ~ 1.0

		jpgWriter.setOutput(ImageIO.createImageOutputStream(jpegOutput));
		jpgWriter.write(null, new IIOImage(processedImage, null, null), jpgWriteParam);
		jpgWriter.dispose();

		byte[] compressedJpegBytes = jpegOutput.toByteArray();

		// PDF 생성
		PDDocument document = new PDDocument();
		PDPage page = new PDPage(new PDRectangle(processedImage.getWidth(), processedImage.getHeight()));
		document.addPage(page);

		PDImageXObject pdImage = PDImageXObject.createFromByteArray(document, compressedJpegBytes, "jpegImage");
		PDPageContentStream contentStream = new PDPageContentStream(document, page);
		contentStream.drawImage(pdImage, 0, 0, processedImage.getWidth(), processedImage.getHeight());
		contentStream.close();

		// byte[] 로도 반환
		ByteArrayOutputStream pdfOutput = new ByteArrayOutputStream();
		document.save(pdfOutput);
		document.close();

		byte[] pdfBytes = pdfOutput.toByteArray();

		String saveFileName = UUID.randomUUID() + ".pdf";
		String pdfPath = contentId + "/" + ApplicationConstants.PDF + "/" + saveFileName;
		s3Service.uploadFile(pdfPath, pdfBytes);

		return pdfRepository.save(
			Pdf.builder()
				.pdfPath(pdfPath)
				.pdfSize((long) pdfBytes.length)
				.width(processedImage.getWidth())
				.height(processedImage.getHeight())
				.saveFileName(saveFileName)
				.originalFileName(multipartFile.getOriginalFilename())
				.build()
			).getPdfUid();
	}

}
