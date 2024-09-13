package com.muzlive.kitpage.kitpage.service.page;

import com.muzlive.kitpage.kitpage.config.exception.CommonException;
import com.muzlive.kitpage.kitpage.config.exception.ExceptionCode;
import com.muzlive.kitpage.kitpage.domain.page.Page;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.ComicBook;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.ComicBookDetail;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.Music;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.repository.ComicBookDetailRepository;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.repository.ComicBookRepository;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.repository.MusicRepository;
import com.muzlive.kitpage.kitpage.domain.page.dto.req.UploadComicBookDetailReq;
import com.muzlive.kitpage.kitpage.domain.page.dto.req.UploadComicBookReq;
import com.muzlive.kitpage.kitpage.domain.page.dto.req.UploadMusicReq;
import com.muzlive.kitpage.kitpage.domain.page.repository.PageRepository;
import com.muzlive.kitpage.kitpage.domain.user.Image;
import com.muzlive.kitpage.kitpage.domain.user.repository.ImageRepository;
import com.muzlive.kitpage.kitpage.service.aws.S3Service;
import com.muzlive.kitpage.kitpage.utils.constants.ApplicationConstants;
import com.muzlive.kitpage.kitpage.utils.enums.ImageCode;
import com.muzlive.kitpage.kitpage.utils.enums.PageContentType;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class PageService {

	private final PageRepository pageRepository;

	private final ComicService comicService;

	private final S3Service s3Service;

	private final ImageRepository imageRepository;

	private final ComicBookRepository comicBookRepository;

	private final ComicBookDetailRepository comicBookDetailRepository;

	private final MusicRepository musicRepository;

	public void insertComicBook(UploadComicBookReq uploadComicBookReq) throws Exception {

		// S3 Cover Image Upload
		String coverImagePath = uploadComicBookReq.getContentId() + "/" + ApplicationConstants.IMAGE + "/" + UUID.randomUUID().toString().substring(0, 8) + "_" + uploadComicBookReq.getCoverImage().getOriginalFilename();
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
						.contentId(uploadComicBookReq.getContentId())
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

	public void insertComicBookDetail(UploadComicBookDetailReq uploadComicBookDetailReq) throws Exception {
		ComicBook comicBook = comicBookRepository.findByContentId(uploadComicBookDetailReq.getContentId())
			.orElseThrow(() -> new CommonException(ExceptionCode.CANNOT_FIND_MATCHED_ITEM));

		for(MultipartFile multipartFile : uploadComicBookDetailReq.getImages()) {
			// S3 Cover Image Upload
			String imagePath = uploadComicBookDetailReq.getContentId() + "/" + ApplicationConstants.CONTENT + "/" + UUID.randomUUID().toString().substring(0, 8) + "_" + multipartFile.getOriginalFilename();
			s3Service.uploadFile(imagePath, multipartFile);

			// Image DB Insert
			Image image = imageRepository.save(Image.of(imagePath, ImageCode.COVER_IMAGE, multipartFile));

			comicService.createComicBookDetail(
				ComicBookDetail.builder()
				.comicBookUid(comicBook.getComicBookUid())
				.title(uploadComicBookDetailReq.getTitle())
				.chapter(uploadComicBookDetailReq.getChapter())
				.imageUid(image.getImageUid())
				.region(uploadComicBookDetailReq.getRegion())
				.build()
			);
		}
	}

	public void insertMusic(UploadMusicReq uploadMusicReq) throws Exception {
		ComicBook comicBook = comicBookRepository.findByContentId(uploadMusicReq.getContentId())
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
			Image image = imageRepository.save(Image.of(coverImagePath, ImageCode.COVER_IMAGE, uploadMusicReq.getImage()));

			music.setCoverImageUid(image.getImageUid());
		}

		musicRepository.save(music);
	}
}
