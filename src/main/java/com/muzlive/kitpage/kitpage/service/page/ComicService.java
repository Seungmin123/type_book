package com.muzlive.kitpage.kitpage.service.page;

import static com.muzlive.kitpage.kitpage.domain.page.QPage.page;
import static com.muzlive.kitpage.kitpage.domain.page.comicbook.QComicBook.comicBook;
import static com.muzlive.kitpage.kitpage.domain.page.comicbook.QComicBookDetail.comicBookDetail;
import static com.muzlive.kitpage.kitpage.domain.user.QImage.image;
import static com.muzlive.kitpage.kitpage.domain.user.QInstallLog.installLog;
import static com.muzlive.kitpage.kitpage.domain.user.QKit.kit;

import com.muzlive.kitpage.kitpage.config.exception.CommonException;
import com.muzlive.kitpage.kitpage.config.exception.ExceptionCode;
import com.muzlive.kitpage.kitpage.domain.page.Page;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.ComicBook;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.ComicBookDetail;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.Video;
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
import com.muzlive.kitpage.kitpage.domain.page.repository.PageRepository;
import com.muzlive.kitpage.kitpage.domain.user.Image;
import com.muzlive.kitpage.kitpage.domain.user.repository.ImageRepository;
import com.muzlive.kitpage.kitpage.domain.user.repository.KitRepository;
import com.muzlive.kitpage.kitpage.service.aws.S3Service;
import com.muzlive.kitpage.kitpage.utils.constants.ApplicationConstants;
import com.muzlive.kitpage.kitpage.utils.enums.ImageCode;
import com.muzlive.kitpage.kitpage.utils.enums.KitStatus;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

	private final FileService fileService;

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
				.imageUid(fileService.uploadConvertFile(comicBook.getPage().getContentId(), multipartFile, ImageCode.COMIC_IMAGE))
				.build());
		}

		if(!CollectionUtils.isEmpty(comicBookDetails)) {
			comicBookDetailRepository.saveAll(comicBookDetails);
		}
	}


	public ComicBookContentResp getComicBookContent(String deviceId, String contentId) throws Exception {
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

	public Page findPageWithComicBooksBySerialNumber(String serialNumber) throws Exception {
		return pageRepository.findWithChildBySerialNumber(serialNumber).orElse(null);
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

	public List<ComicBookDetailResp> getRelatedComicDetailBookList(String deviceId, String contentId) throws Exception {
		List<ComicBookDetailResp> comicBookDetailResps = new ArrayList<>();
		List<Page> pages = pageService.findByContentId(contentId);

		for (Page pageItem : pages) {
			ComicBookDetailResp comicBookDetailResp = new ComicBookDetailResp(pageItem);

			List<VideoResp> videoResps = new ArrayList<>();
			List<ComicBookEpisodeResp> comicBookEpisodeResps = new ArrayList<>();

			//if (this.getInstallStatus(pageItem.getPageUid(), deviceId).equals(KitStatus.AVAILABLE)) {
				videoResps = this.findVideoByPageUid(pageItem.getPageUid()).stream().map(VideoResp::new).collect(Collectors.toList());
				comicBookEpisodeResps = this.getEpisodeResps(pageItem.getPageUid());
			//}

			comicBookDetailResp.setVideos(videoResps);
			comicBookDetailResp.setDetails(comicBookEpisodeResps);

			comicBookDetailResps.add(comicBookDetailResp);
		}

		return comicBookDetailResps;
	}

	// TODO N+1 Check
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
