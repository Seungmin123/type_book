package com.muzlive.kitpage.kitpage.service.page;

import static com.muzlive.kitpage.kitpage.domain.page.QPage.page;
import static com.muzlive.kitpage.kitpage.domain.page.comicbook.QComicBook.comicBook;
import static com.muzlive.kitpage.kitpage.domain.page.comicbook.QComicBookDetail.comicBookDetail;
import static com.muzlive.kitpage.kitpage.domain.user.QImage.image;
import static com.muzlive.kitpage.kitpage.domain.user.QInstallLog.installLog;
import static com.muzlive.kitpage.kitpage.domain.user.QKit.kit;

import com.luciad.imageio.webp.WebPWriteParam;
import com.muzlive.kitpage.kitpage.config.exception.CommonException;
import com.muzlive.kitpage.kitpage.config.exception.ExceptionCode;
import com.muzlive.kitpage.kitpage.domain.page.Content;
import com.muzlive.kitpage.kitpage.domain.page.Page;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.ComicBook;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.ComicBookDetail;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.Video;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp.ComicBookContentResp;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp.ComicBookDetailResp;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp.ComicBookEpisodeResp;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp.ComicBookImageResp;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp.ComicBookRelatedResp;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp.ComicBookResp;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp.VideoResp;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.repository.ComicBookDetailRepository;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.repository.ComicBookRepository;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.repository.VideoRepository;
import com.muzlive.kitpage.kitpage.domain.page.dto.req.UploadComicBookDetailReq;
import com.muzlive.kitpage.kitpage.domain.page.dto.req.UploadComicBookReq;
import com.muzlive.kitpage.kitpage.domain.page.repository.PageRepository;
import com.muzlive.kitpage.kitpage.domain.user.Image;
import com.muzlive.kitpage.kitpage.domain.user.Kit;
import com.muzlive.kitpage.kitpage.domain.user.repository.ImageRepository;
import com.muzlive.kitpage.kitpage.domain.user.repository.KitRepository;
import com.muzlive.kitpage.kitpage.service.aws.S3Service;
import com.muzlive.kitpage.kitpage.utils.constants.ApplicationConstants;
import com.muzlive.kitpage.kitpage.utils.enums.ImageCode;
import com.muzlive.kitpage.kitpage.utils.enums.KitStatus;
import com.muzlive.kitpage.kitpage.utils.enums.Region;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
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
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@Service
public class ComicService {

	private final JPAQueryFactory queryFactory;

	private final PageService pageService;

	private final UserService userService;

	private final S3Service s3Service;

	private final KitRepository kitRepository;

	private final PageRepository pageRepository;

	private final ImageRepository imageRepository;

	private final ComicBookRepository comicBookRepository;

	private final ComicBookDetailRepository comicBookDetailRepository;

	private final VideoRepository videoRepository;

	public ComicBook getComicBook(Long comicBookUid) throws Exception {
		return comicBookRepository.findById(comicBookUid)
			.orElseThrow(() -> new CommonException(ExceptionCode.CANNOT_FIND_ITEM_THAT_MATCH_THE_PARAM));
	}

	public ComicBook upsertComicBook(ComicBook comicBook) throws Exception {
		return comicBookRepository.save(comicBook);
	}

	public ComicBookDetail upsertComicBookDetail(ComicBookDetail comicBookDetail) throws Exception {
		return comicBookDetailRepository.save(comicBookDetail);
	}

	public int findComicBookMaxPage(Long comicBookUid) throws Exception {
		Integer page = comicBookDetailRepository.findMaxPage(comicBookUid);
		return page == null ? 0 : page;
	}

	public List<Video> findVideoByPageUid(Long pageUid) throws Exception {
		List<Video> videos = videoRepository.findByPageUid(pageUid);
		return CollectionUtils.isEmpty(videos) ? new ArrayList<>() : videos;
	}

	@Transactional
	public void insertComicBook(String contentId, UploadComicBookReq uploadComicBookReq) throws Exception {
		// S3 Cover Image Upload
		String saveFileName = UUID.randomUUID() + "." + FilenameUtils.getExtension(uploadComicBookReq.getCoverImage().getOriginalFilename());
		String coverImagePath = contentId + "/" + ApplicationConstants.IMAGE + "/" + saveFileName;
		s3Service.uploadFile(coverImagePath, uploadComicBookReq.getCoverImage());

		// Image DB Insert
		Image image = Image.of(coverImagePath, ImageCode.COMIC_COVER_IMAGE, uploadComicBookReq.getCoverImage());
		image.setSaveFileName(saveFileName);
		imageRepository.save(image);

		// Page, ComicBook DB Insert
		comicBookRepository.save(
			ComicBook.builder()
				.pageUid(uploadComicBookReq.getPageUid())
				.coverImageUid(image.getImageUid())
				.writer(uploadComicBookReq.getWriter())
				.illustrator(uploadComicBookReq.getIllustrator())
				.volume(uploadComicBookReq.getVolume())
				.volumeUnit(uploadComicBookReq.getVolumeUnit() == null ? "%d권" : uploadComicBookReq.getVolumeUnit())
				.pageUnit(uploadComicBookReq.getPageUnit() == null ? "%d쪽" : uploadComicBookReq.getPageUnit())
				.build()
		);
	}

	@Transactional
	public void insertComicBookDetail(UploadComicBookDetailReq uploadComicBookDetailReq) throws Exception {
		ComicBook comicBook = comicBookRepository.findById(uploadComicBookDetailReq.getComicBookUid())
			.orElseThrow(() -> new CommonException(ExceptionCode.CANNOT_FIND_MATCHED_ITEM));

		String episode = Objects.nonNull(uploadComicBookDetailReq.getEpisode()) ? uploadComicBookDetailReq.getEpisode() : "";
		int page = this.findComicBookMaxPage(comicBook.getComicBookUid());

		for(MultipartFile multipartFile : uploadComicBookDetailReq.getImages()) {
			// S3 Cover Image Upload
			String ext = "." + FilenameUtils.getExtension(multipartFile.getOriginalFilename());

			String saveFileName = UUID.randomUUID() + ".webp";
			String imagePath = comicBook.getPage().getContentId() + "/" + ApplicationConstants.CONVERT + "/" + saveFileName;

			// 원본
			String originSaveFileName = saveFileName.replace(".webp", ext);
			String originImagePath = comicBook.getPage().getContentId() + "/" + ApplicationConstants.IMAGE + "/" + originSaveFileName;
			s3Service.uploadFile(originImagePath, multipartFile);

			// Image DB Insert
			Image image = Image.of(imagePath, ImageCode.COMIC_IMAGE, multipartFile);
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

			imageRepository.save(image);

			comicBookDetailRepository.save(ComicBookDetail.builder()
				.comicBookUid(comicBook.getComicBookUid())
				.episode(episode)
				.page(page++)
				.imageUid(image.getImageUid())
				.build());
		}
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

	public ComicBookContentResp getComicBookContent(String deviceId, String contentId, Region region) throws Exception {
		List<Page> pages = pageRepository.findAllWithChild(contentId, region).orElse(new ArrayList<>());
		if(CollectionUtils.isEmpty(pages)) throw new CommonException(ExceptionCode.CANNOT_FIND_MATCHED_ITEM);
		ComicBookContentResp comicBookContentResp = new ComicBookContentResp(pages.get(0).getContent());

		List<ComicBookResp> comicBookResps = new ArrayList<>();
		for (Page pageItem : pages) {
			ComicBookResp comicBookResp = new ComicBookResp(pageItem);
			comicBookResp.setKitStatus(this.getInstallStatus(pageItem.getPageUid(), deviceId));

			// 총 용량, Volume 수정하면 좋을 것 같음
			List<ComicBook> comicBooks = comicBookRepository.findAllByPageUid(pageItem.getPageUid()).orElse(new ArrayList<>());
			if(!CollectionUtils.isEmpty(comicBooks)) {
				comicBookContentResp.setTotalVolume(comicBookContentResp.getTotalVolume() + comicBooks.size());

				long totalSize = comicBooks.stream()
					.mapToLong(this::getComicBookImageSize)
					.sum();
				comicBookResp.setTotalSize(totalSize);
			}

			comicBookResps.add(comicBookResp);

		}
		comicBookContentResp.setComicBookResps(comicBookResps);

		return comicBookContentResp;
	}

	public Page findPageWithComicBooksBySerialNumber(String serialNumber) throws Exception {
		return pageRepository.findWithChildBySerialNumber(serialNumber).orElseThrow(() -> new CommonException(ExceptionCode.CANNOT_FIND_MATCHED_ITEM));
	}

	public Long getImageSizeByPageUid(Long pageUid) throws Exception {
		return Optional.ofNullable(
			queryFactory
				.select(image.imageSize.sum())
				.from(image)
				.innerJoin(comicBookDetail).on(comicBookDetail.imageUid.eq(image.imageUid))
				.innerJoin(comicBook).on(comicBook.comicBookUid.eq(comicBookDetail.comicBookUid))
				.innerJoin(page).on(page.pageUid.eq(comicBook.pageUid))
				.where(page.pageUid.eq(pageUid))
				.fetchFirst()
		).orElse(0L);
	}

	public Long getImageSizeByComicBookUid(Long comicBookUid) throws Exception {
		return Optional.ofNullable(
			queryFactory
				.select(image.imageSize.sum())
				.from(image)
				.innerJoin(comicBookDetail).on(comicBookDetail.imageUid.eq(image.imageUid))
				.innerJoin(comicBook).on(comicBook.comicBookUid.eq(comicBookDetail.comicBookUid))
				.where(comicBook.comicBookUid.eq(comicBookUid))
			.fetchFirst()
		).orElse(0L);
	}

	public Long getComicBookImageSize(ComicBook comicBook) {
		List<ComicBookDetail> comicBookDetails = comicBook.getComicBookDetails();
		if(!CollectionUtils.isEmpty(comicBookDetails)) {
			return comicBookDetails.stream().mapToLong(v -> v.getImage().getImageSize()).sum();
		}

		return 0L;
	}

	public List<ComicBookDetailResp> getRelatedComicDetailBookList(String deviceId, String contentId, Region region) throws Exception {
		List<ComicBookDetailResp> comicBookDetailResps = new ArrayList<>();
		List<Page> pages = pageService.findByContentId(contentId, region);

		for (Page pageItem : pages) {
			ComicBookDetailResp comicBookDetailResp = new ComicBookDetailResp(pageItem);

			List<VideoResp> videoResps = new ArrayList<>();
			List<ComicBookEpisodeResp> comicBookEpisodeResps = new ArrayList<>();

			if (this.getInstallStatus(pageItem.getPageUid(), deviceId).equals(KitStatus.AVAILABLE)) {
				videoResps = this.findVideoByPageUid(pageItem.getPageUid()).stream().map(VideoResp::new).collect(Collectors.toList());
				comicBookEpisodeResps = this.getEpisodeResps(pageItem.getPageUid());
			}

			comicBookDetailResp.setVideos(videoResps);
			comicBookDetailResp.setDetails(comicBookEpisodeResps);

			comicBookDetailResps.add(comicBookDetailResp);
		}

		return comicBookDetailResps;
	}

	public List<ComicBookEpisodeResp> getEpisodeResps(Long pageUid) throws Exception {
		List<ComicBookEpisodeResp> comicBookEpisodeResps = new ArrayList<>();
		List<ComicBook> comicBooks = comicBookRepository.findAllByPageUid(pageUid).orElse(new ArrayList<>());
		for(ComicBook comicBook : comicBooks) {
			ComicBookEpisodeResp comicBookEpisodeResp = new ComicBookEpisodeResp(comicBook, ApplicationConstants.COMIC_BOOK_UNIT_1);

			// 최근 업데이트 날짜 확인을 위해 for 루프 여기서 실행
			if(CollectionUtils.isEmpty(comicBook.getComicBookDetails())){
				comicBookEpisodeResp.setPageSize(0);
				comicBookEpisodeResp.setDetailPages(new ArrayList<>());
			} else {
				LocalDateTime lastModifiedAt = null;

				comicBookEpisodeResp.setPageSize(comicBook.getComicBookDetails().size());

				List<ComicBookImageResp> comicBookImageResps = new ArrayList<>();
				for(ComicBookDetail comicBookDetail : comicBook.getComicBookDetails()) {
					comicBookImageResps.add(ComicBookImageResp.of(comicBookDetail));

					// 최근 이미지 업데이트 날짜. - 클라이언트에서 변경된 파일 확인용
					if(lastModifiedAt == null || lastModifiedAt.isBefore(comicBookDetail.getModifiedAt()))
						lastModifiedAt = comicBookDetail.getModifiedAt();
				}
				comicBookEpisodeResp.setDetailPages(comicBookImageResps);
				comicBookEpisodeResp.setLastModifiedAt(lastModifiedAt);
			}

			comicBookEpisodeResps.add(comicBookEpisodeResp);
		}
		comicBookEpisodeResps.sort(Comparator.comparing(ComicBookEpisodeResp::getVolume));
		return comicBookEpisodeResps;
	}

	public KitStatus getInstallStatus(Long pageUid, List<Tuple> tuples) throws Exception {
		for(Tuple tuple : tuples) {
			if(tuple.get(installLog).getPageUid().equals(pageUid)) {
				if(tuple.get(kit) == null // Install 이력은 있지만 현재 다른 키트에 태그된 상태
					|| tuple.get(installLog).getCreatedAt().plusDays(1).isBefore(LocalDateTime.now())) {
					return KitStatus.EXPIRED;
				} else {
					return KitStatus.AVAILABLE;
				}
			}
		}

		return KitStatus.NEVER_USE;
	}

	public KitStatus getInstallStatus(Long pageUid, String deviceId) throws Exception {
		return kitRepository.findFirstByPageUidAndDeviceIdOrderByCreatedAtDesc(pageUid, deviceId)
			.map(k -> {
				if(k.getModifiedAt().plusDays(1).isBefore(LocalDateTime.now())) {
					return KitStatus.EXPIRED;
				} else {
					return KitStatus.AVAILABLE;
				}
			})
			.orElse(KitStatus.NEVER_USE);
	}

}
