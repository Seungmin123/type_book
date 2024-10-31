package com.muzlive.kitpage.kitpage.service.page;

import static com.muzlive.kitpage.kitpage.domain.page.QPage.page;
import static com.muzlive.kitpage.kitpage.domain.page.comicbook.QComicBook.comicBook;
import static com.muzlive.kitpage.kitpage.domain.page.comicbook.QComicBookDetail.comicBookDetail;
import static com.muzlive.kitpage.kitpage.domain.user.QImage.image;
import static com.muzlive.kitpage.kitpage.domain.user.QKit.kit;
import static com.muzlive.kitpage.kitpage.domain.user.QKitLog.kitLog;
import static com.muzlive.kitpage.kitpage.domain.user.QVersionInfo.versionInfo;

import com.muzlive.kitpage.kitpage.config.exception.CommonException;
import com.muzlive.kitpage.kitpage.config.exception.ExceptionCode;
import com.muzlive.kitpage.kitpage.domain.page.Page;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.ComicBook;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.ComicBookDetail;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.Music;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.Video;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.repository.ComicBookRepository;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.repository.MusicRepository;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.repository.VideoRepository;
import com.muzlive.kitpage.kitpage.domain.page.dto.req.CreatePageReq;
import com.muzlive.kitpage.kitpage.domain.page.dto.req.UploadComicBookDetailReq;
import com.muzlive.kitpage.kitpage.domain.page.dto.req.UploadComicBookReq;
import com.muzlive.kitpage.kitpage.domain.page.dto.req.UploadMusicReq;
import com.muzlive.kitpage.kitpage.domain.page.dto.req.UploadVideoReq;
import com.muzlive.kitpage.kitpage.domain.page.repository.PageRepository;
import com.muzlive.kitpage.kitpage.domain.user.Image;
import com.muzlive.kitpage.kitpage.domain.user.VersionInfo;
import com.muzlive.kitpage.kitpage.domain.user.dto.req.VersionInfoReq;
import com.muzlive.kitpage.kitpage.domain.user.dto.resp.VersionInfoResp;
import com.muzlive.kitpage.kitpage.domain.user.repository.ImageRepository;
import com.muzlive.kitpage.kitpage.service.aws.S3Service;
import com.muzlive.kitpage.kitpage.utils.constants.ApplicationConstants;
import com.muzlive.kitpage.kitpage.utils.enums.ImageCode;
import com.muzlive.kitpage.kitpage.utils.enums.PageContentType;
import com.muzlive.kitpage.kitpage.utils.enums.VideoCode;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.micrometer.core.instrument.util.StringUtils;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class PageService {

	private final JPAQueryFactory queryFactory;

	private final S3Service s3Service;

	private final PageRepository pageRepository;

	private final ImageRepository imageRepository;

	private final ComicBookRepository comicBookRepository;

	private final MusicRepository musicRepository;

	private final VideoRepository videoRepository;

	public List<Page> findByContentId(String contentId) throws Exception {
		return pageRepository.findByContentId(contentId)
			.orElseThrow(() -> new CommonException(ExceptionCode.CANNOT_FIND_MATCHED_ITEM));
	}

	public Page findPageById(Long pageUid) throws Exception {
		return pageRepository.findById(pageUid).orElseThrow(() -> new CommonException(ExceptionCode.CANNOT_FIND_MATCHED_ITEM));
	}

	public Image findImageById(Long imageUid) throws Exception {
		return imageRepository.findById(imageUid).orElseThrow(() -> new CommonException(ExceptionCode.CANNOT_FIND_MATCHED_ITEM));
	}

	public List<Image> findByPageUid(Long pageUid) throws Exception {
		return queryFactory.select(image)
			.from(image)
				.innerJoin(comicBook).on(comicBook.pageUid.eq(pageUid))
				.innerJoin(comicBookDetail).on(comicBookDetail.comicBookUid.eq(comicBook.comicBookUid)
					.and(comicBookDetail.imageUid.eq(image.imageUid)))
			.orderBy(comicBook.volume.asc(), comicBookDetail.page.asc())
			.fetch();
	}

	public List<Page> findByDeviceId(String deviceId) throws Exception {
		List<Page> pages = queryFactory
			.selectFrom(page)
				.innerJoin(kit).on(kit.pageUid.eq(page.pageUid))
			.where(kit.deviceId.eq(deviceId))
			.fetch();

		if(CollectionUtils.isEmpty(pages)) pages = new ArrayList<>();

		return pages;
	}

	@Transactional
	public Video upsertVideo(Video video) throws Exception {
		return videoRepository.save(video);
	}

	@Transactional
	public void createPage(CreatePageReq createPageReq) throws Exception {
		String contentId = createPageReq.getContentId();

		if(StringUtils.isEmpty(contentId)) {
			Long nextPageUid = pageRepository.findFirstByOrderByPageUidDesc()
				.map(page -> page.getPageUid() != null ? page.getPageUid() + 1L : 1L)
				.orElse(1L);

			contentId = createPageReq.getContentType() + "_" + String.format("%08d", nextPageUid);
		}

		// S3 Cover Image Upload
		String saveFileName = UUID.randomUUID() + "." + FilenameUtils.getExtension(createPageReq.getCoverImage().getOriginalFilename());
		String coverImagePath = contentId + "/" + ApplicationConstants.IMAGE + "/" + saveFileName;
		s3Service.uploadFile(coverImagePath, createPageReq.getCoverImage());

		// Image DB Insert
		Image image = Image.of(coverImagePath, ImageCode.PAGE_IMAGE, createPageReq.getCoverImage());
		image.setSaveFileName(saveFileName);
		imageRepository.save(image);

		pageRepository.save(
			Page.builder()
			.contentId(contentId)
			.contentType(createPageReq.getContentType())
			.coverImageUid(image.getImageUid())
			.title(createPageReq.getTitle())
			.subTitle(createPageReq.getSubtitle())
			.infoText(createPageReq.getInfoText())
			.company(createPageReq.getCompany())
			.genre(createPageReq.getGenre())
			.region(createPageReq.getRegion())
			.build());
	}

	public void insertMusic(UploadMusicReq uploadMusicReq) throws Exception {
		comicBookRepository.findByPageContentId(uploadMusicReq.getContentId())
			.orElseThrow(() -> new CommonException(ExceptionCode.CANNOT_FIND_MATCHED_ITEM));

		// S3 Music Upload
		String saveMusicName = UUID.randomUUID() + "." + FilenameUtils.getExtension(uploadMusicReq.getFile().getOriginalFilename());
		String filePath = uploadMusicReq.getContentId() + "/" + ApplicationConstants.MUSIC + "/" + saveMusicName;
		s3Service.uploadFile(filePath, uploadMusicReq.getFile());

		Music music = Music.builder()
			.contentId(uploadMusicReq.getContentId())
			.album(uploadMusicReq.getAlbum())
			.artist(uploadMusicReq.getArtist())
			.title(uploadMusicReq.getTitle())
			.filePath(filePath)
			.saveFileName(saveMusicName)
			.originalFileName(uploadMusicReq.getFile().getOriginalFilename())
			// TODO
			.playTime("")
			.build();

		if(Objects.nonNull(uploadMusicReq.getImage())) {
			// S3 Cover Image Upload
			String saveFileName = UUID.randomUUID() + "." + FilenameUtils.getExtension(uploadMusicReq.getImage().getOriginalFilename());
			String coverImagePath = uploadMusicReq.getContentId() + "/" + ApplicationConstants.MUSIC + "/" + saveFileName;
			s3Service.uploadFile(coverImagePath, uploadMusicReq.getImage());

			// Image DB Insert
			Image image = Image.of(coverImagePath, ImageCode.MUSIC_COVER_IMAGE, uploadMusicReq.getImage());
			image.setSaveFileName(saveFileName);
			imageRepository.save(image);

			music.setCoverImageUid(image.getImageUid());
		}

		musicRepository.save(music);
	}

	public Video insertVideo(UploadVideoReq uploadVideoReq) throws Exception {

		String filePath = null;

		comicBookRepository.findByPageContentId(uploadVideoReq.getContentId())
			.orElseThrow(() -> new CommonException(ExceptionCode.CANNOT_FIND_MATCHED_ITEM));

		String streamUrl = uploadVideoReq.getStreamUrl();
		VideoCode videoCode = VideoCode.STREAM;

		if(Objects.nonNull(uploadVideoReq.getFile())) {
			// S3 Cover Image Upload
			String saveFileName = UUID.randomUUID() + "." + FilenameUtils.getExtension(uploadVideoReq.getFile().getOriginalFilename());
			filePath = uploadVideoReq.getContentId() + "/" + ApplicationConstants.VIDEO + "/" + saveFileName;
			s3Service.uploadDecryptFile(filePath, uploadVideoReq.getFile());

			streamUrl = filePath;
			videoCode = VideoCode.S3;
		}

		Video video = Video.builder()
			.contentId(uploadVideoReq.getContentId())
			.artist(uploadVideoReq.getArtist())
			.title(uploadVideoReq.getTitle())
			.streamUrl(streamUrl)
			.videoCode(videoCode)
			.build();

		if(Objects.nonNull(uploadVideoReq.getImage())) {
			// S3 Cover Image Upload
			String saveFileName = UUID.randomUUID() + "." + FilenameUtils.getExtension(uploadVideoReq.getImage().getOriginalFilename());
			String coverImagePath = uploadVideoReq.getContentId() + "/" + ApplicationConstants.VIDEO + "/" + saveFileName;
			s3Service.uploadFile(coverImagePath, uploadVideoReq.getImage());

			// Image DB Insert
			Image image = Image.of(coverImagePath, ImageCode.VIDEO_COVER_IMAGE, uploadVideoReq.getImage());
			image.setSaveFileName(saveFileName);
			imageRepository.save(image);

			video.setCoverImageUid(image.getImageUid());
		}

		videoRepository.save(video);

		return video;
	}

	public VersionInfoResp getVersionInfo(VersionInfoReq versionInfoReq) throws Exception {
		// Client Test 용
		if(versionInfoReq.getCurrentVersion().endsWith("-dev") || versionInfoReq.getCurrentVersion().endsWith("-test"))
			return new VersionInfoResp(false, false);

		if(!this.isVersionFormat(versionInfoReq.getCurrentVersion())
			|| !this.isVersionFormat(versionInfoReq.getOsVersion()))
			throw new CommonException(ExceptionCode.INVALID_REQUEST_PRAMETER);

		VersionInfoResp versionInfoResp;

		VersionInfo version = queryFactory
			.selectFrom(versionInfo)
			.where(versionInfo.osType.eq(versionInfoReq.getPlatform()))
			.orderBy(versionInfo.versionInfoUid.desc())
			.fetchFirst();

		return Optional.ofNullable(version).map(v -> {
			String latestVersion = v.getLatestVersion();
			String forcedVersion = v.getForcedVersion();
			String osVersion = v.getOsVersion();

			int currentVersionCompare = versionInfoReq.CurrentVersionCompareTo(latestVersion);
			Boolean needUpdate = currentVersionCompare < 0;
			Boolean isForced = (versionInfoReq.isCurrentVersionLessThanTo(forcedVersion) && versionInfoReq.isOsVersionGreaterThanTo(osVersion));

			return new VersionInfoResp(needUpdate, isForced);
		}).orElse(null);
	}

	private boolean isVersionFormat(String version) {
		String pattern = "^\\d+(\\.\\d+)?(\\.\\d+)?$";
		return version.matches(pattern);
	}
}
