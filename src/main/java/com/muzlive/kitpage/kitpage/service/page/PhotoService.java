package com.muzlive.kitpage.kitpage.service.page;

import com.muzlive.kitpage.kitpage.config.exception.CommonException;
import com.muzlive.kitpage.kitpage.config.exception.ExceptionCode;
import com.muzlive.kitpage.kitpage.domain.page.dto.req.UploadPhotoBookDetailReq;
import com.muzlive.kitpage.kitpage.domain.page.dto.req.UploadPhotoBookReq;
import com.muzlive.kitpage.kitpage.domain.page.photobook.PhotoBook;
import com.muzlive.kitpage.kitpage.domain.page.photobook.PhotoBookDetail;
import com.muzlive.kitpage.kitpage.domain.page.photobook.repository.PhotoBookDetailRepository;
import com.muzlive.kitpage.kitpage.domain.page.photobook.repository.PhotoBookRepository;
import com.muzlive.kitpage.kitpage.utils.enums.ImageCode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
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

	private final FileService fileService;

	private final PhotoBookRepository photoBookRepository;

	private final PhotoBookDetailRepository photoBookDetailRepository;

	@Transactional
	public PhotoBook insertPhotoBook(String contentId, UploadPhotoBookReq uploadPhotoBookReq) throws Exception {
		return photoBookRepository.save(
			PhotoBook.builder()
				.pageUid(uploadPhotoBookReq.getPageUid())
				.coverImageUid(fileService.uploadConvertFile(contentId, uploadPhotoBookReq.getCoverImage(), ImageCode.PHOTO_IMAGE))
				.title(uploadPhotoBookReq.getTitle())
				.build()
		);
	}

	@Transactional
	public void insertPhotoBookDetail(UploadPhotoBookDetailReq uploadPhotoBookDetailReq) throws Exception {
		if(uploadPhotoBookDetailReq.getPath() != null && !uploadPhotoBookDetailReq.getPath().isEmpty()) {
			PhotoBook photoBook = photoBookRepository.findById(uploadPhotoBookDetailReq.getPhotoBookUid())
				.orElseThrow(() -> new CommonException(ExceptionCode.CANNOT_FIND_MATCHED_ITEM));

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
									.imageUid(fileService.uploadConvertFile(photoBook.getPage().getContentId(), multipartFile, ImageCode.PHOTO_IMAGE))
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
						.imageUid(fileService.uploadConvertFile(photoBook.getPage().getContentId(), multipartFile, ImageCode.PHOTO_IMAGE))
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

}
