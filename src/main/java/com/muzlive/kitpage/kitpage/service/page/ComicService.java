package com.muzlive.kitpage.kitpage.service.page;

import static com.muzlive.kitpage.kitpage.domain.page.comicbook.QComicBook.comicBook;
import static com.muzlive.kitpage.kitpage.domain.user.QKit.kit;

import com.muzlive.kitpage.kitpage.config.exception.CommonException;
import com.muzlive.kitpage.kitpage.config.exception.ExceptionCode;
import com.muzlive.kitpage.kitpage.domain.page.Page;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.ComicBook;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.ComicBookDetail;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp.ComicBookDetailResp;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp.ComicBookEpisodeResp;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp.ComicBookRelatedResp;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp.ComicBookResp;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.repository.ComicBookDetailRepository;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.repository.ComicBookRepository;
import com.muzlive.kitpage.kitpage.domain.page.dto.req.UploadComicBookDetailReq;
import com.muzlive.kitpage.kitpage.domain.page.dto.req.UploadComicBookReq;
import com.muzlive.kitpage.kitpage.domain.user.Image;
import com.muzlive.kitpage.kitpage.domain.user.KitLog;
import com.muzlive.kitpage.kitpage.domain.user.repository.ImageRepository;
import com.muzlive.kitpage.kitpage.service.aws.S3Service;
import com.muzlive.kitpage.kitpage.utils.constants.ApplicationConstants;
import com.muzlive.kitpage.kitpage.utils.enums.ImageCode;
import com.muzlive.kitpage.kitpage.utils.enums.KitStatus;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class ComicService {

	private final JPAQueryFactory queryFactory;

	private final PageService pageService;

	private final KitService kitService;

	private final S3Service s3Service;

	private final ImageRepository imageRepository;

	private final ComicBookRepository comicBookRepository;

	private final ComicBookDetailRepository comicBookDetailRepository;

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
			String saveFileName = UUID.randomUUID() + "." + FilenameUtils.getExtension(multipartFile.getOriginalFilename());
			String coverImagePath = comicBook.getPage().getContentId() + "/" + ApplicationConstants.IMAGE + "/" + saveFileName;
			s3Service.uploadFile(coverImagePath, multipartFile);

			// Image DB Insert
			Image image = Image.of(coverImagePath, ImageCode.COMIC_IMAGE, multipartFile);
			image.setSaveFileName(saveFileName);
			imageRepository.save(image);

			comicBookDetailRepository.save(ComicBookDetail.builder()
				.comicBookUid(comicBook.getComicBookUid())
				.episode(episode)
				.page(page++)
				.imageUid(image.getImageUid())
				.build());
		}
	}

	public ComicBookRelatedResp getRelatedComicBookList(Long pageUid, String deviceId) throws Exception {
		ComicBookRelatedResp comicBookRelatedResp = new ComicBookRelatedResp();

		Page page = pageService.findPageById(pageUid);
		List<Page> pages = pageService.findByContentId(page.getContentId());
		List<KitLog> kitLogs = kitService.getInstalledStatus(page.getContentId(), deviceId);

		List<ComicBookResp> comicBookResps = new ArrayList<>();
		for (Page pageItem : pages) {
			ComicBookResp comicBookResp = new ComicBookResp(pageItem);
			comicBookResp.setKitStatus(this.getInstallStatus(pageItem.getPageUid(), kitLogs));

			if(pageItem.getPageUid().equals(pageUid))
				comicBookRelatedResp.setTaggedComicBook(comicBookResp);

			comicBookResps.add(comicBookResp);
		}
		comicBookRelatedResp.setComicBookResps(comicBookResps);

		return comicBookRelatedResp;
	}

	public List<ComicBookEpisodeResp> getEpisodeResps(Page page) throws Exception {
		List<ComicBookEpisodeResp> comicBookEpisodeResps = new ArrayList<>();
		for(ComicBook comicBook : page.getComicBooks()) {
			comicBookEpisodeResps.add(new ComicBookEpisodeResp(comicBook, ApplicationConstants.COMIC_BOOK_UNIT_1));
		}
		comicBookEpisodeResps.sort(Comparator.comparing(ComicBookEpisodeResp::getVolume));
		return comicBookEpisodeResps;
	}

	public KitStatus getInstallStatus(Long pageUid, List<KitLog> kitLogs) throws Exception {
		return kitLogs.stream()
			.filter(v -> v.getPageUid().equals(pageUid))
			.findFirst()
			.map(v -> {
				if (v.getCreatedAt().plusDays(1).isBefore(LocalDateTime.now())) {
					return KitStatus.EXPIRED;
				} else {
					return KitStatus.AVAILABLE;
				}
			})
			.orElse(KitStatus.NEVER_USE);
	}


}
