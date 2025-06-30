package com.muzlive.kitpage.kitpage.service.page;

import static com.muzlive.kitpage.kitpage.domain.page.QPage.page;
import static com.muzlive.kitpage.kitpage.domain.page.photobook.QPdf.pdf;
import static com.muzlive.kitpage.kitpage.domain.page.photobook.QPhotoBook.photoBook;
import static com.muzlive.kitpage.kitpage.domain.page.photobook.QPhotoBookDetail.photoBookDetail;
import static com.muzlive.kitpage.kitpage.domain.user.QImage.image;

import com.muzlive.kitpage.kitpage.config.exception.CommonException;
import com.muzlive.kitpage.kitpage.config.exception.ExceptionCode;
import com.muzlive.kitpage.kitpage.domain.page.Page;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp.VideoResp;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.repository.VideoRepository;
import com.muzlive.kitpage.kitpage.domain.page.dto.req.UploadPhotoBookDetailReq;
import com.muzlive.kitpage.kitpage.domain.page.dto.req.UploadPhotoBookReq;
import com.muzlive.kitpage.kitpage.domain.page.photobook.PhotoBook;
import com.muzlive.kitpage.kitpage.domain.page.photobook.PhotoBookDetail;
import com.muzlive.kitpage.kitpage.domain.page.photobook.dto.resp.PhotoBookCommonEpisodeDetailResp;
import com.muzlive.kitpage.kitpage.domain.page.photobook.dto.resp.PhotoBookContentResp;
import com.muzlive.kitpage.kitpage.domain.page.photobook.dto.resp.PhotoBookDetailResp;
import com.muzlive.kitpage.kitpage.domain.page.photobook.dto.resp.PhotoBookEpisodeDetailResp;
import com.muzlive.kitpage.kitpage.domain.page.photobook.dto.resp.PhotoBookEpisodeResp;
import com.muzlive.kitpage.kitpage.domain.page.photobook.dto.resp.PhotoBookImageResp;
import com.muzlive.kitpage.kitpage.domain.page.photobook.dto.resp.PhotoBookResp;
import com.muzlive.kitpage.kitpage.domain.page.photobook.repository.PhotoBookDetailRepository;
import com.muzlive.kitpage.kitpage.domain.page.photobook.repository.PhotoBookRepository;
import com.muzlive.kitpage.kitpage.domain.page.repository.ContentRepository;
import com.muzlive.kitpage.kitpage.domain.page.repository.PageRepository;
import com.muzlive.kitpage.kitpage.utils.enums.ClientPlatformType;
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
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class PhotoService {

	private final JPAQueryFactory queryFactory;

	private final FileService fileService;

	private final ContentRepository contentRepository;

	private final PageRepository pageRepository;

	private final VideoRepository videoRepository;

	private final PhotoBookRepository photoBookRepository;

	private final PhotoBookDetailRepository photoBookDetailRepository;

	// TODO 사실상 중복코드들 추상화 필요

	public PhotoBookContentResp getPhotoBookContent(String contentId) {
		List<Page> pages = pageRepository.findAllWithPhotoBooks(contentId);
		if(CollectionUtils.isEmpty(pages)) throw new CommonException(ExceptionCode.CANNOT_FIND_MATCHED_ITEM);
		PhotoBookContentResp photoBookContentResp = new PhotoBookContentResp(pages.get(0).getContent());

		List<PhotoBookResp> photoBookResps = new ArrayList<>();
		for (Page pageItem : pages) {
			PhotoBookResp photoBookResp = new PhotoBookResp(pageItem);
			photoBookResp.setKitStatus(KitStatus.NEVER_USE);

			List<PhotoBook> photoBooks = pageItem.getPhotoBooks();
			if(!CollectionUtils.isEmpty(photoBooks)) {
				photoBookContentResp.setTotalVolume(photoBookContentResp.getTotalVolume() + photoBooks.size());

				long totalSize = photoBookRepository.sumImageSizeByPageUid(pageItem.getPageUid()).orElse(0L);
				photoBookResp.setTotalSize(totalSize);
			}

			photoBookResps.add(photoBookResp);
		}
		photoBookContentResp.setPhotoBookResps(photoBookResps);

		return photoBookContentResp;
	}

	public List<PhotoBookDetailResp> getRelatedPhotoDetailBookList(String contentId, ClientPlatformType clientPlatformType) {
		List<PhotoBookDetailResp> photoBookDetailResps = new ArrayList<>();

		contentRepository.findByContentId(contentId).ifPresent(content -> {
			for (Page pageItem : content.getPages()) {
				photoBookDetailResps.add(this.getPhotoBookDetail(pageItem, clientPlatformType));
			}
		});

		return photoBookDetailResps;
	}

	public PhotoBookDetailResp getPhotoBookDetail(Page page, ClientPlatformType clientPlatformType) {
		PhotoBookDetailResp photoBookDetailResp = new PhotoBookDetailResp(page);
		photoBookDetailResp.setVideos(this.findVideoByPageUid(page.getPageUid()));
		photoBookDetailResp.setDetails(this.getEpisodeResps(page.getPageUid(), clientPlatformType));
		return photoBookDetailResp;
	}

	private List<PhotoBookEpisodeResp> getEpisodeResps(Long pageUid, ClientPlatformType clientPlatformType) {
		// TODO Client 별로 Response 가 다름. -> 리팩토링 필요
		List<PhotoBook> photoBooks = clientPlatformType.equals(ClientPlatformType.IOS)
			? photoBookRepository.findAllWithPdfByPageUid(pageUid).orElse(new ArrayList<>())
			: photoBookRepository.findAllWithImageByPageUid(pageUid).orElse(new ArrayList<>());

		return photoBooks.stream()
			.map(photoBook -> {
				List<PhotoBookCommonEpisodeDetailResp> details = new ArrayList<>();
				LocalDateTime lastModifiedAt = null;

				for(PhotoBookDetail detail : photoBook.getPhotoBookDetails()) {
					if (ClientPlatformType.IOS.equals(clientPlatformType)) {
						details.add(PhotoBookEpisodeDetailResp.of(detail));
					} else {
						details.add(PhotoBookImageResp.of(detail));
					}

					LocalDateTime modifiedAt = detail.getModifiedAt();
					if (modifiedAt != null && (lastModifiedAt == null || modifiedAt.isAfter(
						lastModifiedAt))) {
						lastModifiedAt = modifiedAt;
					}
				}

				PhotoBookEpisodeResp resp = new PhotoBookEpisodeResp(photoBook);
				resp.setPageSize(CollectionUtils.isEmpty(details) ? 0 : details.size());
				resp.setDetailPages(details);
				resp.setLastModifiedAt(lastModifiedAt);

				return resp;
			})
			.sorted(Comparator.comparing(PhotoBookEpisodeResp::getPhotoBookUid))
			.collect(Collectors.toList());
	}

	public List<VideoResp> findVideoByPageUid(Long pageUid) {
		return videoRepository.findByPageUid(pageUid).stream().map(VideoResp::new).collect(
			Collectors.toList());
	}

	public Long getImageSizeByPageUid(Long pageUid, ClientPlatformType clientPlatformType) {
		if(clientPlatformType.equals(ClientPlatformType.IOS)) {
			return Optional.ofNullable(
				queryFactory
					.select(pdf.pdfSize.sum())
					.from(pdf)
					.innerJoin(photoBookDetail).on(photoBookDetail.pdfUid.eq(pdf.pdfUid))
					.innerJoin(photoBook).on(photoBook.photoBookUid.eq(photoBookDetail.photoBookUid))
					.innerJoin(page).on(page.pageUid.eq(photoBook.pageUid))
					.where(page.pageUid.eq(pageUid))
					.fetchFirst()
			).orElse(0L);
		} else {
			return Optional.ofNullable(
				queryFactory
					.select(image.imageSize.sum())
					.from(image)
					.innerJoin(photoBookDetail).on(photoBookDetail.imageUid.eq(image.imageUid))
					.innerJoin(photoBook).on(photoBook.photoBookUid.eq(photoBookDetail.photoBookUid))
					.innerJoin(page).on(page.pageUid.eq(photoBook.pageUid))
					.where(page.pageUid.eq(pageUid))
					.fetchFirst()
			).orElse(0L);
		}
	}

	// Admin ************************************************************************************************************
	@Transactional
	public PhotoBook insertPhotoBook(String contentId, UploadPhotoBookReq uploadPhotoBookReq) throws Exception {
		return photoBookRepository.save(
			PhotoBook.builder()
				.pageUid(uploadPhotoBookReq.getPageUid())
				.coverImageUid(fileService.uploadConvertImageFile(contentId, uploadPhotoBookReq.getCoverImage(), ImageCode.PHOTO_IMAGE))
				.title(uploadPhotoBookReq.getTitle())
				.build()
		);
	}

	@Transactional
	public void insertPhotoBookDetail(UploadPhotoBookDetailReq uploadPhotoBookDetailReq) throws Exception {
		if(uploadPhotoBookDetailReq.getPath() != null && !uploadPhotoBookDetailReq.getPath().isEmpty()) {
			PhotoBook photoBook = photoBookRepository.findById(uploadPhotoBookDetailReq.getPhotoBookUid())
				.orElseThrow(() -> new CommonException(ExceptionCode.CANNOT_FIND_MATCHED_ITEM));

			String contentId = photoBook.getPage().getContentId();
			AtomicInteger page = new AtomicInteger(this.findPhotoBookMaxPage(photoBook.getPhotoBookUid()));
			List<PhotoBookDetail> photoBookDetails = new ArrayList<>();

			try (Stream<Path> paths = Files.walk(Paths.get(uploadPhotoBookDetailReq.getPath()))) {
				paths.filter(Files::isRegularFile)
					.sorted(Comparator.comparing(path -> path.getFileName().toString()))
					.forEach(file -> {
						try {
							MultipartFile multipartFile = convert(file);
							String ext = FilenameUtils.getExtension(multipartFile.getOriginalFilename());

							if(ext != null && !ext.equals("DS_Store")) {
								photoBookDetails.add(
									PhotoBookDetail.builder()
									.photoBookUid(photoBook.getPhotoBookUid())
									.page(page.getAndIncrement())
									.imageUid(fileService.uploadConvertImageFile(contentId, multipartFile, ImageCode.PHOTO_IMAGE))
									.pdfUid(fileService.convertImageBytesToPdf(contentId, multipartFile, 0.7f))
									.build());
							}
						} catch (Exception ignore){}
					});
			}

			photoBookDetailRepository.saveAll(photoBookDetails);

		} else if (!CollectionUtils.isEmpty(uploadPhotoBookDetailReq.getImages())) {
			PhotoBook photoBook = photoBookRepository.findById(uploadPhotoBookDetailReq.getPhotoBookUid())
				.orElseThrow(() -> new CommonException(ExceptionCode.CANNOT_FIND_MATCHED_ITEM));

			int page = this.findPhotoBookMaxPage(photoBook.getPhotoBookUid());

			List<PhotoBookDetail> photoBookDetails = new ArrayList<>();
			for(MultipartFile multipartFile : uploadPhotoBookDetailReq.getImages()) {
				photoBookDetails.add(
					PhotoBookDetail.builder()
						.photoBookUid(photoBook.getPhotoBookUid())
						.page(page++)
						.imageUid(fileService.uploadConvertImageFile(photoBook.getPage().getContentId(), multipartFile, ImageCode.PHOTO_IMAGE))
						.build()
				);
			}

			photoBookDetailRepository.saveAll(photoBookDetails);
		}
	}

	public int findPhotoBookMaxPage(Long comicBookUid) {
		Integer page = photoBookDetailRepository.findMaxPage(comicBookUid);
		return page == null ? 0 : page + 1;
	}

	private MultipartFile convert(Path path) throws Exception {
		String name = path.toFile().getName();
		String originalFileName = path.toFile().getName();
		String contentType = Files.probeContentType(path);
		byte[] content = Files.readAllBytes(path);
		MultipartFile multipartFile = new MockMultipartFile(name, originalFileName, contentType, content);

		return multipartFile;
	}

	// Admin ************************************************************************************************************

}
