package com.muzlive.kitpage.kitpage.service.page;

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
import com.muzlive.kitpage.kitpage.domain.page.dto.req.UploadComicBookDetailReq;
import com.muzlive.kitpage.kitpage.domain.page.dto.req.UploadComicBookReq;
import com.muzlive.kitpage.kitpage.domain.page.dto.req.UploadMusicReq;
import com.muzlive.kitpage.kitpage.domain.page.dto.req.UploadVideoReq;
import com.muzlive.kitpage.kitpage.domain.page.repository.PageRepository;
import com.muzlive.kitpage.kitpage.domain.user.Image;
import com.muzlive.kitpage.kitpage.domain.user.dto.req.VersionInfoReq;
import com.muzlive.kitpage.kitpage.domain.user.dto.resp.VersionInfoResp;
import com.muzlive.kitpage.kitpage.domain.user.repository.ImageRepository;
import com.muzlive.kitpage.kitpage.service.aws.S3Service;
import com.muzlive.kitpage.kitpage.utils.constants.ApplicationConstants;
import com.muzlive.kitpage.kitpage.utils.enums.ImageCode;
import com.muzlive.kitpage.kitpage.utils.enums.PageContentType;
import com.muzlive.kitpage.kitpage.utils.enums.VideoCode;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class PageService {

	private final JPAQueryFactory queryFactory;

	private final ComicService comicService;

	private final S3Service s3Service;

	private final PageRepository pageRepository;

	private final ImageRepository imageRepository;

	private final ComicBookRepository comicBookRepository;

	private final MusicRepository musicRepository;

	private final VideoRepository videoRepository;

	@Transactional
	public void insertComicBook(UploadComicBookReq uploadComicBookReq) throws Exception {

		Long nextPageUid = pageRepository.findFirstByOrderByPageUidDesc()
			.map(page -> page.getPageUid() != null ? page.getPageUid() + 1L : 1L)
			.orElse(1L);

		String contentId = ApplicationConstants.COMIC + "_" + String.format("%08d", nextPageUid);

		// S3 Cover Image Upload
		String coverImagePath = contentId + "/" + ApplicationConstants.IMAGE + "/" + UUID.randomUUID().toString().substring(0, 8) + "_" + uploadComicBookReq.getCoverImage().getOriginalFilename();
		s3Service.uploadFile(coverImagePath, uploadComicBookReq.getCoverImage());

		// Image DB Insert
		Image image = imageRepository.save(Image.of(coverImagePath, ImageCode.COVER_IMAGE, uploadComicBookReq.getCoverImage()));

		// Page, ComicBook DB Insert
		comicBookRepository.save(
			ComicBook.builder()
				.writer(uploadComicBookReq.getWriter())
				.illustrator(uploadComicBookReq.getIllustrator())
				.page(
					Page.builder()
						.contentId(contentId)
						.contentType(PageContentType.COMICBOOK)
						.coverImageUid(image.getImageUid())
						.title(uploadComicBookReq.getTitle())
						.subTitle(uploadComicBookReq.getSubtitle())
						.infoText(uploadComicBookReq.getInfoText())
						.company(uploadComicBookReq.getCompany())
						.genre(uploadComicBookReq.getGenre())
						.region(uploadComicBookReq.getRegion())
						.build()
				)
				.build()
		);
	}

	@Transactional
	public void insertComicBookDetail(UploadComicBookDetailReq uploadComicBookDetailReq) throws Exception {
		ComicBook comicBook = comicBookRepository.findByPageContentId(uploadComicBookDetailReq.getContentId())
			.orElseThrow(() -> new CommonException(ExceptionCode.CANNOT_FIND_MATCHED_ITEM));

		String episode = Objects.nonNull(uploadComicBookDetailReq.getEpisode()) ? uploadComicBookDetailReq.getEpisode() : "";
		int page = comicService.findComicBookMaxPage(comicBook.getComicBookUid(), uploadComicBookDetailReq.getVolume());

		for(MultipartFile multipartFile : uploadComicBookDetailReq.getImages()) {
			// S3 Cover Image Upload
			String imagePath = uploadComicBookDetailReq.getContentId() + "/" + ApplicationConstants.CONTENT + "/" + UUID.randomUUID().toString().substring(0, 8) + "_" + multipartFile.getOriginalFilename();
			s3Service.uploadFile(imagePath, multipartFile);

			// Image DB Insert
			Image image = imageRepository.save(Image.of(imagePath, ImageCode.COVER_IMAGE, multipartFile));

			comicService.upsertComicBookDetail(ComicBookDetail.builder()
				.comicBookUid(comicBook.getComicBookUid())
				.volume(uploadComicBookDetailReq.getVolume())
				.episode(episode)
				.page(page++)
				.imageUid(image.getImageUid())
				.build());
		}
	}

	public void insertMusic(UploadMusicReq uploadMusicReq) throws Exception {
		ComicBook comicBook = comicBookRepository.findByPageContentId(uploadMusicReq.getContentId())
			.orElseThrow(() -> new CommonException(ExceptionCode.CANNOT_FIND_MATCHED_ITEM));

		// S3 Music Upload
		String filePath = uploadMusicReq.getContentId() + "/" + ApplicationConstants.MUSIC + "/" + UUID.randomUUID().toString().substring(0, 8) + "_" + uploadMusicReq.getFile().getOriginalFilename();
		s3Service.uploadFile(filePath, uploadMusicReq.getFile());

		Music music = Music.builder()
			.contentId(uploadMusicReq.getContentId())
			.album(uploadMusicReq.getAlbum())
			.artist(uploadMusicReq.getArtist())
			.title(uploadMusicReq.getTitle())
			.filePath(filePath)
			// TODO
			.playTime("")
			.build();

		if(Objects.nonNull(uploadMusicReq.getImage())) {
			// S3 Cover Image Upload
			String coverImagePath = uploadMusicReq.getContentId() + "/" + ApplicationConstants.MUSIC + "/" + UUID.randomUUID().toString().substring(0, 8) + "_" + uploadMusicReq.getImage().getOriginalFilename();
			s3Service.uploadFile(coverImagePath, uploadMusicReq.getImage());

			// Image DB Insert
			Image image = imageRepository.save(Image.of(coverImagePath, ImageCode.MUSIC_COVER_IMAGE, uploadMusicReq.getImage()));

			music.setCoverImageUid(image.getImageUid());
		}

		musicRepository.save(music);
	}

	public void insertVideo(UploadVideoReq uploadVideoReq) throws Exception {

		ComicBook comicBook = comicBookRepository.findByPageContentId(uploadVideoReq.getContentId())
			.orElseThrow(() -> new CommonException(ExceptionCode.CANNOT_FIND_MATCHED_ITEM));

		String streamUrl = uploadVideoReq.getStreamUrl();
		VideoCode videoCode = VideoCode.STREAM;

		if(Objects.nonNull(uploadVideoReq.getFile())) {
			// S3 Music Upload
			String filePath = uploadVideoReq.getContentId() + "/" + ApplicationConstants.VIDEO + "/" + UUID.randomUUID().toString().substring(0, 8) + "_" + uploadVideoReq.getFile().getOriginalFilename();
			s3Service.uploadFile(filePath, uploadVideoReq.getFile());

			streamUrl = filePath;
			videoCode = VideoCode.S3;
		}

		Video video = Video.builder()
			.contentId(uploadVideoReq.getContentId())
			.artist(uploadVideoReq.getArtist())
			.title(uploadVideoReq.getTitle())
			.streamUrl(streamUrl)
			.videCode(videoCode)
			.build();

		if(Objects.nonNull(uploadVideoReq.getImage())) {
			// S3 Cover Image Upload
			String coverImagePath = uploadVideoReq.getContentId() + "/" + ApplicationConstants.VIDEO + "/" + UUID.randomUUID().toString().substring(0, 8) + "_" + uploadVideoReq.getImage().getOriginalFilename();
			s3Service.uploadFile(coverImagePath, uploadVideoReq.getImage());

			// Image DB Insert
			Image image = imageRepository.save(Image.of(coverImagePath, ImageCode.VIDEO_COVER_IMAGE, uploadVideoReq.getImage()));

			video.setCoverImageUid(image.getImageUid());
		}

		videoRepository.save(video);
	}

	public VersionInfoResp getVersionInfo(VersionInfoReq versionInfoReq) throws Exception {
		/*// Client Test 용
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

//            if(currentVersionCompare > 0) {
//                version.setLatestVersion(versionInfoReq.getCurrentVersion());
//                versionInfoRepository.save(version);
//            }

			return new VersionInfoResp(needUpdate, isForced);
		}).orElse(null);*/

		return null;
	}

	private boolean isVersionFormat(String version) {
		String pattern = "^\\d+(\\.\\d+)?(\\.\\d+)?$";
		if (!version.matches(pattern)) {
			return false;
		}

		return true;
	}
}
