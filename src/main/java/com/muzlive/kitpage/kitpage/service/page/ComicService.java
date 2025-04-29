package com.muzlive.kitpage.kitpage.service.page;

import static com.muzlive.kitpage.kitpage.domain.page.QPage.page;
import static com.muzlive.kitpage.kitpage.domain.page.comicbook.QComicBook.comicBook;
import static com.muzlive.kitpage.kitpage.domain.page.comicbook.QComicBookDetail.comicBookDetail;
import static com.muzlive.kitpage.kitpage.domain.user.QImage.image;

import com.muzlive.kitpage.kitpage.config.exception.CommonException;
import com.muzlive.kitpage.kitpage.config.exception.ExceptionCode;
import com.muzlive.kitpage.kitpage.domain.page.Page;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.ComicBook;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.ComicBookDetail;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp.ComicBookContentResp;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp.ComicBookDetailResp;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp.ComicBookEpisodeResp;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp.ComicBookImageResp;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp.ComicBookResp;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp.VideoResp;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.repository.ComicBookDetailRepository;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.repository.ComicBookRepository;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.repository.VideoRepository;
import com.muzlive.kitpage.kitpage.domain.page.dto.req.UploadComicBookDetailReq;
import com.muzlive.kitpage.kitpage.domain.page.dto.req.UploadComicBookReq;
import com.muzlive.kitpage.kitpage.domain.page.dto.resp.CommonEpisodeResp;
import com.muzlive.kitpage.kitpage.domain.page.repository.ContentRepository;
import com.muzlive.kitpage.kitpage.domain.page.repository.PageRepository;
import com.muzlive.kitpage.kitpage.domain.user.Image;
import com.muzlive.kitpage.kitpage.domain.user.repository.ImageRepository;
import com.muzlive.kitpage.kitpage.service.aws.s3.S3Service;
import com.muzlive.kitpage.kitpage.service.page.converter.ComicBookEpisodeConverter;
import com.muzlive.kitpage.kitpage.utils.constants.ApplicationConstants;
import com.muzlive.kitpage.kitpage.utils.enums.ImageCode;
import com.muzlive.kitpage.kitpage.utils.enums.KitStatus;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@Service
public class ComicService {

	private final JPAQueryFactory queryFactory;

	private final FileService fileService;

	private final S3Service s3Service;

	private final ComicBookEpisodeConverter comicBookEpisodeConverter;

	private final ContentRepository contentRepository;

	private final PageRepository pageRepository;

	private final ImageRepository imageRepository;

	private final ComicBookRepository comicBookRepository;

	private final ComicBookDetailRepository comicBookDetailRepository;

	private final VideoRepository videoRepository;

	public int findComicBookMaxPage(Long comicBookUid) {
		Integer page = comicBookDetailRepository.findMaxPage(comicBookUid);
		return page == null ? 0 : page + 1;
	}

	public List<VideoResp> findVideoByPageUid(Long pageUid) {
		return videoRepository.findByPageUid(pageUid).stream().map(VideoResp::new).collect(Collectors.toList());
	}

	@Transactional
	public ComicBook insertComicBook(String contentId, UploadComicBookReq uploadComicBookReq) throws Exception {
		// S3 Cover Image Upload
		String saveFileName = UUID.randomUUID() + "." + FilenameUtils.getExtension(uploadComicBookReq.getCoverImage().getOriginalFilename());
		String coverImagePath = contentId + "/" + ApplicationConstants.IMAGE + "/" + saveFileName;
		s3Service.uploadFile(coverImagePath, uploadComicBookReq.getCoverImage());

		// Image DB Insert
		Image image = Image.of(coverImagePath, ImageCode.COMIC_COVER_IMAGE, uploadComicBookReq.getCoverImage());
		image.setSaveFileName(saveFileName);
		imageRepository.save(image);

		// Page, ComicBook DB Insert
		return comicBookRepository.save(
			ComicBook.builder()
				.pageUid(uploadComicBookReq.getPageUid())
				.coverImageUid(image.getImageUid())
				.volume(uploadComicBookReq.getVolume())
				.build()
		);
	}

	@Transactional
	public void insertComicBookDetail(UploadComicBookDetailReq uploadComicBookDetailReq) throws Exception {
		if(uploadComicBookDetailReq.getPath() != null && !uploadComicBookDetailReq.getPath().isEmpty()) {
			ComicBook comicBook = comicBookRepository.findById(uploadComicBookDetailReq.getComicBookUid())
				.orElseThrow(() -> new CommonException(ExceptionCode.CANNOT_FIND_MATCHED_ITEM));

			String episode = Objects.nonNull(uploadComicBookDetailReq.getEpisode()) ? uploadComicBookDetailReq.getEpisode() : "";
			AtomicInteger page = new AtomicInteger(this.findComicBookMaxPage(comicBook.getComicBookUid()));
			List<ComicBookDetail> comicBookDetails = new ArrayList<>();

			try (Stream<Path> paths = Files.walk(Paths.get(uploadComicBookDetailReq.getPath()))) {
				paths.filter(Files::isRegularFile)
					.forEach(file -> {
						try {
							MultipartFile multipartFile = convert(file);
							String ext = FilenameUtils.getExtension(multipartFile.getOriginalFilename());

							if(ext != null && !ext.equals("DS_Store")) {
								comicBookDetails.add(ComicBookDetail.builder()
									.comicBookUid(comicBook.getComicBookUid())
									.episode(episode)
									.page(page.getAndIncrement())
									.imageUid(fileService.uploadConvertImageFile(comicBook.getPage().getContentId(), multipartFile, ImageCode.COMIC_IMAGE))
									.build());
							}
						} catch (Exception ignore){}
					});
			} finally {
				comicBookDetailRepository.saveAll(comicBookDetails);
			}

		} else if (!CollectionUtils.isEmpty(uploadComicBookDetailReq.getImages())) {
			ComicBook comicBook = comicBookRepository.findById(uploadComicBookDetailReq.getComicBookUid())
				.orElseThrow(() -> new CommonException(ExceptionCode.CANNOT_FIND_MATCHED_ITEM));

			String episode = Objects.nonNull(uploadComicBookDetailReq.getEpisode()) ? uploadComicBookDetailReq.getEpisode() : "";
			int page = this.findComicBookMaxPage(comicBook.getComicBookUid());

			List<ComicBookDetail> comicBookDetails = new ArrayList<>();
			for(MultipartFile multipartFile : uploadComicBookDetailReq.getImages()) {
				comicBookDetails.add(ComicBookDetail.builder()
					.comicBookUid(comicBook.getComicBookUid())
					.episode(episode)
					.page(page++)
					.imageUid(fileService.uploadConvertImageFile(comicBook.getPage().getContentId(), multipartFile, ImageCode.COMIC_IMAGE))
					.build());
			}

			if(!CollectionUtils.isEmpty(comicBookDetails)) {
				comicBookDetailRepository.saveAll(comicBookDetails);
			}
		}
	}

	private MultipartFile convert(Path path) throws Exception {
		String name = path.toFile().getName();
		String originalFileName = path.toFile().getName();
		String contentType = Files.probeContentType(path);
		byte[] content = Files.readAllBytes(path);
		MultipartFile multipartFile = new MockMultipartFile(name, originalFileName, contentType, content);

		return multipartFile;
	}

	public ComicBookContentResp getComicBookContent(String contentId) {
		List<Page> pages = pageRepository.findAllWithComicBooks(contentId);
		if(CollectionUtils.isEmpty(pages)) throw new CommonException(ExceptionCode.CANNOT_FIND_MATCHED_ITEM);
		ComicBookContentResp comicBookContentResp = new ComicBookContentResp(pages.get(0).getContent());

		List<ComicBookResp> comicBookResps = new ArrayList<>();
		for (Page pageItem : pages) {
			ComicBookResp comicBookResp = new ComicBookResp(pageItem);
			comicBookResp.setKitStatus(KitStatus.NEVER_USE);

			List<ComicBook> comicBooks = pageItem.getComicBooks();
			if(!CollectionUtils.isEmpty(comicBooks)) {
				comicBookContentResp.setTotalVolume(comicBookContentResp.getTotalVolume() + comicBooks.size());

				long totalSize = comicBookRepository.sumImageSizeByPageUid(pageItem.getPageUid()).orElse(0L);
				comicBookResp.setTotalSize(totalSize);
			}

			comicBookResps.add(comicBookResp);

		}
		comicBookContentResp.setComicBookResps(comicBookResps);

		return comicBookContentResp;
	}

	public Long getImageSizeByPageUid(Long pageUid) {
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

	public List<ComicBookDetailResp> getRelatedComicDetailBookList(String contentId) {
		List<ComicBookDetailResp> comicBookDetailResps = new ArrayList<>();

		contentRepository.findByContentId(contentId).ifPresent(content -> {
			for (Page pageItem : content.getPages()) {
				comicBookDetailResps.add(this.getComicBookDetail(pageItem));
			}
		});

		return comicBookDetailResps;
	}

	public ComicBookDetailResp getComicBookDetail(Page page) {
		ComicBookDetailResp comicBookDetailResp = new ComicBookDetailResp(page);
		comicBookDetailResp.setDetails(this.getEpisodeResps(page.getPageUid()));
		comicBookDetailResp.setVideos(this.findVideoByPageUid(page.getPageUid()));
		return comicBookDetailResp;
	}

	public List<ComicBookEpisodeResp> getEpisodeResps(Long pageUid) {
		List<ComicBook> comicBooks = comicBookRepository.findAllByPageUid(pageUid).orElse(new ArrayList<>());

		return comicBooks.stream()
			.map(comicBook -> {
				ComicBookEpisodeResp resp = new ComicBookEpisodeResp(comicBook);
				CommonEpisodeResp commonEpisodeResp = comicBookEpisodeConverter.convert(comicBook, null);
				resp.setPageSize(commonEpisodeResp.getPageSize());
				resp.setDetailPages(commonEpisodeResp.getDetailPages());
				resp.setLastModifiedAt(commonEpisodeResp.getLastModifiedAt());
				return resp;
			})
			.sorted(Comparator.comparing(ComicBookEpisodeResp::getVolume))
			.collect(Collectors.toList());
	}

	public KitStatus getInstallStatus(Long pageUid, String deviceId) throws Exception {
//		return kitRepository.findFirstByPageUidAndDeviceIdOrderByCreatedAtDesc(pageUid, deviceId)
//			.map(k -> {
//				if(k.getModifiedAt().plusDays(1).isBefore(LocalDateTime.now())) {
//					return KitStatus.EXPIRED;
//				} else {
//					return KitStatus.AVAILABLE;
//				}
//			})
//			.orElse(KitStatus.NEVER_USE);
		return KitStatus.NEVER_USE;
	}

}
