package com.muzlive.kitpage.kitpage.service.page;

import static com.muzlive.kitpage.kitpage.domain.page.QContent.content;
import static com.muzlive.kitpage.kitpage.domain.page.QPage.page;
import static com.muzlive.kitpage.kitpage.domain.page.comicbook.QComicBook.comicBook;
import static com.muzlive.kitpage.kitpage.domain.page.comicbook.QComicBookDetail.comicBookDetail;
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
import com.muzlive.kitpage.kitpage.domain.page.dto.req.ContentListReq;
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
import com.muzlive.kitpage.kitpage.service.aws.s3.S3Service;
import com.muzlive.kitpage.kitpage.utils.CommonUtils;
import com.muzlive.kitpage.kitpage.utils.constants.ApplicationConstants;
import com.muzlive.kitpage.kitpage.utils.enums.ImageCode;
import com.muzlive.kitpage.kitpage.utils.enums.VideoCode;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
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

	public Content findContentByContentId(String contentId) {
		return contentRepository.findByContentId(contentId).orElseThrow(() -> new CommonException(ExceptionCode.CANNOT_FIND_MATCHED_ITEM));
	}

	public Page findPageBySerialNumber(String serialNumber) {
		return pageRepository.findBySerialNumber(serialNumber).orElseThrow(() -> new CommonException(ExceptionCode.CANNOT_FIND_MATCHED_ITEM));
	}

	public Page findPageBySerialNumberOrElseNull(String serialNumber) {
		return pageRepository.findBySerialNumber(serialNumber).orElse(null);
	}

	public Page findPageById(Long pageUid) {
		return pageRepository.findById(pageUid).orElseThrow(() -> new CommonException(ExceptionCode.CANNOT_FIND_MATCHED_ITEM));
	}

	public Image findImageById(Long imageUid) throws Exception {
		return imageRepository.findById(imageUid).orElseThrow(() -> new CommonException(ExceptionCode.CANNOT_FIND_MATCHED_ITEM));
	}

	public List<Content> findContentList(ContentListReq contentListReq) throws Exception {
		long contentUid = contentListReq.isDescending() ? Long.MAX_VALUE : Long.MIN_VALUE;

		BooleanBuilder whereCondition = new BooleanBuilder();
		whereCondition.and(kit.deviceId.eq(contentListReq.getDeviceId()));
		whereCondition.and(contentListReq.isDescending()
			? content.contentUid.lt(contentUid)
			: content.contentUid.gt(contentUid)) ;

		if(!StringUtils.isEmpty(contentListReq.getSearchValue())) {
			whereCondition.and(content.title.contains(contentListReq.getSearchValue()));
		}

		List<Tuple> result = queryFactory
			.select(content, kit.modifiedAt)
			.from(content)
				.innerJoin(page).on(page.contentId.eq(content.contentId))
				.innerJoin(kit).on(kit.pageUid.eq(page.pageUid))
			.where(whereCondition)
			.orderBy(contentListReq.isDescending() ? kit.modifiedAt.desc() : kit.modifiedAt.asc())
			.limit(contentListReq.getListSize())
			.distinct()
			.fetch();

		return result.stream()
			.map(tuple -> tuple.get(content))
			.distinct()
			.collect(Collectors.toList());

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
	public Video upsertVideo(Video video) {
		return videoRepository.save(video);
	}

	@Transactional
	public List<Kit> createKit(List<CreateKitReq> createKitReqs) throws Exception {
		List<Kit> kits = new ArrayList<>();

		for(CreateKitReq createKitReq : createKitReqs) {
			if(kitRepository.existsBySerialNumber(createKitReq.getSerialNumber())) throw new CommonException(ExceptionCode.ALREADY_EXIST_SERIAL_NUMBER_KIT);

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
		String albumId = createPageReq.getAlbumId();

		if(StringUtils.isEmpty(createPageReq.getAlbumId())) {
			Optional<String> maxIdOpt = pageRepository.findMaxAlbumIdByPrefix(contentId);

			if (maxIdOpt.isEmpty()) {
				albumId = "KP_" + contentId + "_01";
			} else {
				String maxId = maxIdOpt.get();

				String numberPart = maxId.substring(3 + contentId.length() + 1);
				int number = Integer.parseInt(numberPart);
				int nextNumber = number + 1;

				albumId = String.format("%s_%02d", "KP_" + contentId, nextNumber);
			}
		}

		return pageRepository.save(
			Page.builder()
			.contentId(contentId)
			.albumId(albumId)
			.coverImageUid(fileService.uploadConvertImageFile(contentId, createPageReq.getCoverImage(), ImageCode.PAGE_IMAGE))
			.title(createPageReq.getTitle())
			.subTitle(createPageReq.getSubtitle() == null ? "" : createPageReq.getSubtitle())
			.infoText(createPageReq.getInfoText())
			.build());
	}

	@Transactional
	public void createContent(CreateContentReq createContentReq) throws Exception {
		String contentId = createContentReq.getContentId();
		if(StringUtils.isEmpty(contentId)) {
			String prefix = String.valueOf(createContentReq.getContentType());
			Optional<String> maxIdOpt = pageRepository.findMaxContentIdByPrefix(createContentReq.getContentType().getCode());

			if (maxIdOpt.isEmpty()) {
				contentId = prefix + "_00000001";
			} else {
				String maxId = maxIdOpt.get();

				String numberPart = maxId.substring(maxId.indexOf(prefix + "_") + prefix.length() + 1);
				int number = Integer.parseInt(numberPart);
				int nextNumber = number + 1;

				contentId = String.format("%s_%08d", prefix, nextNumber);
			}
		}

		contentRepository.save(Content.builder()
				.contentId(contentId)
				.contentType(createContentReq.getContentType())
				.company(createContentReq.getCompany() == null ? "" : createContentReq.getCompany())
				.title(createContentReq.getTitle())
				.writer(createContentReq.getWriter())
				.illustrator(createContentReq.getIllustrator())
				.volumeUnit(createContentReq.getVolumeUnit() == null ? "%d권" : createContentReq.getVolumeUnit())
				.pageUnit(createContentReq.getPageUnit() == null ? "%d쪽" : createContentReq.getPageUnit())
				.readingDirection(createContentReq.getReadingDirection())
				.infoText(createContentReq.getInfoText())
				.coverImageUid(fileService.uploadConvertImageFile(contentId, createContentReq.getImage(), ImageCode.CONTENT_COVER_IMAGE))
				.region(createContentReq.getRegion())
				.genreList(createContentReq.getGenreList())
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

	@Transactional
	public Video insertVideo(UploadVideoReq uploadVideoReq, VideoCode videoCode) {
		Page page = pageRepository.findById(uploadVideoReq.getPageUid()).orElseThrow(() -> new CommonException(ExceptionCode.CANNOT_FIND_MATCHED_ITEM));
		String streamUrl = Objects.nonNull(uploadVideoReq.getVideoFilePath()) ? uploadVideoReq.getVideoFilePath() : uploadVideoReq.getStreamUrl();

		return this.upsertVideo(Video.builder()
			.contentId(uploadVideoReq.getContentId())
			.pageUid(uploadVideoReq.getPageUid())
			.duration(uploadVideoReq.getDuration() == null ? "" : this.formatSeconds(Integer.parseInt(uploadVideoReq.getDuration())))
			.title(uploadVideoReq.getTitle() == null ? "" : uploadVideoReq.getTitle())
			.streamUrl(streamUrl)
			.videoCode(videoCode)
			.pageUid(page.getPageUid())
			.page(page)
			.build());

	}

	private String formatSeconds(int totalSeconds) {
		int hours = totalSeconds / 3600;
		int minutes = (totalSeconds % 3600) / 60;
		int seconds = totalSeconds % 60;

		String minuteStr = String.format("%02d", minutes);
		String secondStr = String.format("%02d", seconds);

		if (hours > 0) {
			return String.format("%d:%s:%s", hours, minuteStr, secondStr);
		} else {
			return String.format("%s:%s", minuteStr, secondStr);
		}
	}

	public Long uploadThumbnail(String contentId, String s3Key) throws Exception {
		try (InputStream inputStream = s3Service.downloadFile(s3Key)) {
			return saveImage(contentId, FilenameUtils.getExtension(s3Key), commonUtils.inputStreamToByteArray(inputStream));
		} catch(Exception e) {
			log.error(e.getMessage());
			throw new CommonException(ExceptionCode.THUMBNAIL_UPLOAD_ERROR);
		}
	}

	public Long uploadYoutubeThumbnail(String contentId, String url) throws Exception {
		byte[] thumbnail = this.downloadFileFromUrl(url);
		return this.saveImage(contentId, FilenameUtils.getExtension(url), thumbnail);
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

	public Long saveImage(String contentId, String ext, byte[] image) throws Exception {
		String saveFileName = UUID.randomUUID() + "." + ext;
		String coverImagePath = contentId + "/" + ApplicationConstants.VIDEO + "/" + saveFileName;
		s3Service.uploadFile(coverImagePath, image);

		BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(image));
		return imageRepository.save(
			Image.builder()
			.imagePath(coverImagePath)
			.imageCode(ImageCode.VIDEO_COVER_IMAGE)
			.imageSize(image.length == 0 ? 0 : (long) image.length)
			.width(bufferedImage.getWidth())
			.height(bufferedImage.getHeight())
			.originalFileName("")
			.saveFileName(saveFileName)
			.md5(DigestUtils.md5Hex(image))
			.build()
		).getImageUid();
	}

	public VersionInfoResp getVersionInfo(VersionInfoReq versionInfoReq) throws Exception {
		// Client Test 용
		if(versionInfoReq.getCurrentVersion().endsWith("-dev") || versionInfoReq.getCurrentVersion().endsWith("-test"))
			return new VersionInfoResp(false, false);

		versionInfoReq.setCurrentVersion(this.extractVersionFormat(versionInfoReq.getCurrentVersion()));

		if(this.invalidVersionFormat(versionInfoReq.getCurrentVersion())
			|| this.invalidVersionFormat(versionInfoReq.getOsVersion()))
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

			Boolean needUpdate = versionInfoReq.isCurrentVersionLessThanTo(latestVersion);
			Boolean isForced = versionInfoReq.isCurrentVersionLessThanTo(forcedVersion) && versionInfoReq.isOsVersionGreaterThanTo(osVersion);

			return new VersionInfoResp(needUpdate, isForced);
		}).orElse(null);
	}

	private boolean invalidVersionFormat(String version) {
		String pattern = "^\\d+(\\.\\d+)?(\\.\\d+)?$";
		return !version.matches(pattern);
	}

	private String extractVersionFormat(String version) throws Exception {
		// 정규식: x.x.x 또는 x.x 포맷
		String versionPattern = "^\\d+(\\.\\d+)+";
		Matcher matcher = Pattern.compile(versionPattern).matcher(version);

		if (matcher.find()) {
			return matcher.group();
		}
		throw new CommonException(ExceptionCode.INVALID_REQUEST_PRAMETER);
	}
}
