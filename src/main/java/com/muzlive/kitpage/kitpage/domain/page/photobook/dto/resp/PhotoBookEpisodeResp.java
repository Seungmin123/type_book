package com.muzlive.kitpage.kitpage.domain.page.photobook.dto.resp;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.muzlive.kitpage.kitpage.config.serializer.LocalDateTimeSerializer;
import com.muzlive.kitpage.kitpage.domain.page.photobook.PhotoBook;
import com.muzlive.kitpage.kitpage.utils.enums.ReadingDirection;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PhotoBookEpisodeResp {

	private Long photoBookUid;

	private Long coverImageUid;

	private String title;

	private String pageUnit;

	private Integer pageSize;

	private ReadingDirection readingDirection;

	private List<PhotoBookImageResp> detailPages;

	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime lastModifiedAt;

	public PhotoBookEpisodeResp(PhotoBook photoBook) {
		this.photoBookUid = photoBook.getPhotoBookUid();
		this.coverImageUid = photoBook.getCoverImageUid();
		this.title = photoBook.getTitle();
		this.readingDirection = photoBook.getPage().getContent().getReadingDirection();
	}

}
