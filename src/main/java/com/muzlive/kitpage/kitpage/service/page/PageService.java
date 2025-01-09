package com.muzlive.kitpage.kitpage.service.page;

import static com.muzlive.kitpage.kitpage.domain.page.QPage.page;
import static com.muzlive.kitpage.kitpage.domain.page.comicbook.QComicBook.comicBook;
import static com.muzlive.kitpage.kitpage.domain.page.comicbook.QComicBookDetail.comicBookDetail;
import static com.muzlive.kitpage.kitpage.domain.user.QImage.image;
import static com.muzlive.kitpage.kitpage.domain.user.QKit.kit;
import static com.muzlive.kitpage.kitpage.domain.user.QVersionInfo.versionInfo;

import com.muzlive.kitpage.kitpage.config.exception.CommonException;
import com.muzlive.kitpage.kitpage.config.exception.ExceptionCode;
import com.muzlive.kitpage.kitpage.domain.page.Content;
import com.muzlive.kitpage.kitpage.domain.page.Page;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.Music;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.Video;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.repository.ComicBookRepository;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.repository.MusicRepository;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.repository.VideoRepository;
import com.muzlive.kitpage.kitpage.domain.page.dto.req.CreateContentReq;
import com.muzlive.kitpage.kitpage.domain.page.dto.req.CreateKitReq;
import com.muzlive.kitpage.kitpage.domain.page.dto.req.CreatePageReq;
import com.muzlive.kitpage.kitpage.domain.page.dto.req.UploadMusicReq;
import com.muzlive.kitpage.kitpage.domain.page.dto.req.UploadVideoReq;
import com.muzlive.kitpage.kitpage.domain.page.repository.ContentRepository;
import com.muzlive.kitpage.kitpage.domain.page.repository.PageRepository;
import com.muzlive.kitpage.kitpage.domain.user.Image;
import com.muzlive.kitpage.kitpage.domain.user.Kit;
import com.muzlive.kitpage.kitpage.domain.user.VersionInfo;
import com.muzlive.kitpage.kitpage.domain.user.dto.req.VersionInfoReq;
import com.muzlive.kitpage.kitpage.domain.user.dto.resp.VersionInfoResp;
import com.muzlive.kitpage.kitpage.domain.user.repository.ImageRepository;
import com.muzlive.kitpage.kitpage.domain.user.repository.KitRepository;
import com.muzlive.kitpage.kitpage.service.aws.S3Service;
import com.muzlive.kitpage.kitpage.utils.CommonUtils;
import com.muzlive.kitpage.kitpage.utils.constants.ApplicationConstants;
import com.muzlive.kitpage.kitpage.utils.enums.ImageCode;
import com.muzlive.kitpage.kitpage.utils.enums.Region;
import com.muzlive.kitpage.kitpage.utils.enums.VideoCode;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.micrometer.core.instrument.util.StringUtils;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Slf4j
@RequiredArgsConstructor
@Service
public class PageService {

	private final JPAQueryFactory queryFactory;

	private final S3Service s3Service;

	private final FileService fileService;

	private final KitRepository kitRepository;

	private final ContentRepository contentRepository;

	private final PageRepository pageRepository;

	private final ImageRepository imageRepository;

	private final ComicBookRepository comicBookRepository;

	private final MusicRepository musicRepository;

	private final VideoRepository videoRepository;

	private final CommonUtils commonUtils;

	public Content findContentByContentId(String contentId, Region region) throws Exception {
		return contentRepository.findByContentIdAndRegion(contentId, region)
			.orElseThrow(() -> new CommonException(ExceptionCode.CANNOT_FIND_MATCHED_ITEM));
	}

	public List<Page> findByContentId(String contentId) throws Exception {
		return pageRepository.findAllByContentId(contentId)
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

	public boolean existsByImageUidAndSerialNumber(Long imageUid, String serialNumber) throws Exception {
		return queryFactory
			.selectOne()
			.from(kit)
				.innerJoin(comicBook).on(comicBook.pageUid.eq(kit.pageUid))
				.innerJoin(comicBookDetail).on(comicBookDetail.comicBookUid.eq(comicBook.comicBookUid))
			.where(kit.serialNumber.eq(serialNumber)
				.and(comicBookDetail.imageUid.eq(imageUid)))
			.fetchFirst() != null;
	}

	@Transactional
	public Video upsertVideo(Video video) throws Exception {
		return videoRepository.save(video);
	}

	@Transactional
	public List<Kit> createKit(List<CreateKitReq> createKitReqs) throws Exception {
		List<Kit> kits = new ArrayList<>();

		for(CreateKitReq createKitReq : createKitReqs) {
			if(kitRepository.existBySerialNumber(createKitReq.getSerialNumber())) throw new CommonException(ExceptionCode.ALREADY_EXIST_SERIAL_NUMBER_KIT);

			Page page = pageRepository.findByAlbumId(createKitReq.getAppId()).orElseThrow(() -> new CommonException(ExceptionCode.CANNOT_FIND_MATCHED_ITEM));

			kits.add(new Kit(createKitReq.getSerialNumber(), page.getPageUid()));
		}
		
		if(!CollectionUtils.isEmpty(kits)) {
			return kitRepository.saveAll(kits);
		}

		return null;
	}

	@Transactional
	public List<Kit> updateKit(List<CreateKitReq> createKitReqs) throws Exception {
		List<Kit> kits = new ArrayList<>();

		for(CreateKitReq createKitReq : createKitReqs) {
			Kit kit = kitRepository.findBySerialNumber(createKitReq.getSerialNumber()).orElseThrow(() -> new CommonException(ExceptionCode.CANNOT_FIND_MATCHED_ITEM));
			Page page = pageRepository.findByAlbumId(createKitReq.getAppId()).orElseThrow(() -> new CommonException(ExceptionCode.CANNOT_FIND_MATCHED_ITEM));
			kit.setPageUid(page.getPageUid());
			kits.add(kit);
		}

		if(!CollectionUtils.isEmpty(kits)) {
			return kitRepository.saveAll(kits);
		}

		return null;
	}

	@Transactional
	public void deleteKit(List<CreateKitReq> createKitReqs) throws Exception {
		List<Kit> kits = new ArrayList<>();

		for(CreateKitReq createKitReq : createKitReqs) {
			Kit kit = kitRepository.findBySerialNumber(createKitReq.getSerialNumber()).orElseThrow(() -> new CommonException(ExceptionCode.CANNOT_FIND_MATCHED_ITEM));
			kits.add(kit);
		}

		if(!CollectionUtils.isEmpty(kits)) {
			kitRepository.deleteAll(kits);
		}
	}

	@Transactional
	public Page createPage(CreatePageReq createPageReq) throws Exception {
		String contentId = createPageReq.getContentId();

		if(StringUtils.isEmpty(contentId)) {
			Long nextPageUid = pageRepository.findFirstByOrderByPageUidDesc()
				.map(page -> page.getPageUid() != null ? page.getPageUid() + 1L : 1L)
				.orElse(1L);

			contentId = ApplicationConstants.PAGE_APP_ID_SEPARATOR + "_" + createPageReq.getContentType() + "_" + String.format("%08d", nextPageUid);
		}

		// S3 Cover Image Upload
		String saveFileName = UUID.randomUUID() + "." + FilenameUtils.getExtension(createPageReq.getCoverImage().getOriginalFilename());
		String coverImagePath = contentId + "/" + ApplicationConstants.IMAGE + "/" + saveFileName;
		s3Service.uploadFile(coverImagePath, createPageReq.getCoverImage());

		// Image DB Insert
		Image image = Image.of(coverImagePath, ImageCode.PAGE_IMAGE, createPageReq.getCoverImage());
		image.setSaveFileName(saveFileName);
		imageRepository.save(image);

		return pageRepository.save(
			Page.builder()
			.contentId(contentId)
			.contentType(createPageReq.getContentType())
			.coverImageUid(image.getImageUid())
			.title(createPageReq.getTitle())
			.subTitle(createPageReq.getSubtitle())
			.infoText(createPageReq.getInfoText())
			.company(createPageReq.getCompany())
			.region(createPageReq.getRegion())
			.build());
	}

	@Transactional
	public void createContent(CreateContentReq createContentReq) throws Exception {
		contentRepository.save(Content.builder()
				.contentId(createContentReq.getContentId())
				.contentType(createContentReq.getContentType())
				.title(createContentReq.getTitle())
				.infoText(createContentReq.getInfoText())
				.coverImageUid(fileService.uploadConvertFile(createContentReq.getContentId(), createContentReq.getImage(), ImageCode.CONTENT_COVER_IMAGE))
				.region(createContentReq.getRegion())
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
		VideoCode videoCode = VideoCode.YOUTUBE;

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
			.duration(uploadVideoReq.getDuration() == null ? "" : uploadVideoReq.getDuration())
			.title(uploadVideoReq.getTitle() == null ? "" : uploadVideoReq.getTitle())
			.streamUrl(streamUrl)
			.videoCode(videoCode)
			.pageUid(uploadVideoReq.getPageUid())
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

	public byte[] downloadFileFromUrl(String fileUrl) throws Exception {
		URL url = new URL(fileUrl);
		URLConnection connection = url.openConnection();
		try (InputStream inputStream = connection.getInputStream()) {
			return commonUtils.inputStreamToByteArray(inputStream);
		} catch(Exception e) {
			log.error(e.getMessage());
			return null;
		}
	}

	public Long uploadYoutubeThumbnail(String contentId, String url) throws Exception {
		byte[] thumbnail = this.downloadFileFromUrl(url);

		String saveFileName = UUID.randomUUID() + "." + FilenameUtils.getExtension(url);
		String coverImagePath = contentId + "/" + ApplicationConstants.VIDEO + "/" + saveFileName;
		s3Service.uploadFile(coverImagePath, thumbnail);

		BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(thumbnail));
		Image image = Image.builder()
			.imagePath(coverImagePath)
			.imageCode(ImageCode.VIDEO_COVER_IMAGE)
			.imageSize(thumbnail.length == 0 ? 0 : (long) thumbnail.length)
			.width(bufferedImage.getWidth())
			.height(bufferedImage.getHeight())
			.originalFileName("")
			.saveFileName(saveFileName)
			.md5(DigestUtils.md5Hex(thumbnail))
			.build();

		return imageRepository.save(image).getImageUid();
	}

	public VersionInfoResp getVersionInfo(VersionInfoReq versionInfoReq) throws Exception {
		// Client Test 용
		if(versionInfoReq.getCurrentVersion().endsWith("-dev") || versionInfoReq.getCurrentVersion().endsWith("-test"))
			return new VersionInfoResp(false, false);

		if(!this.isVersionFormat(versionInfoReq.getCurrentVersion())
			|| !this.isVersionFormat(versionInfoReq.getOsVersion()))
			throw new CommonException(ExceptionCode.INVALID_REQUEST_PRAMETER);

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
