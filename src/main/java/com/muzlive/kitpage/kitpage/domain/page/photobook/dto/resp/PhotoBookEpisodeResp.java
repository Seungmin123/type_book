package com.muzlive.kitpage.kitpage.domain.page.photobook.dto.resp;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.muzlive.kitpage.kitpage.config.serializer.LocalDateTimeSerializer;
import com.muzlive.kitpage.kitpage.domain.page.photobook.PhotoBook;
import com.muzlive.kitpage.kitpage.utils.enums.ReadingDirection;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Schema(name = "PhotoBookEpisodeResp", description = "웹화보 상세 volume 단위 정보")
@Getter
@Setter
public class PhotoBookEpisodeResp {

	private Long photoBookUid;

	private String title;

	private Long coverImageUid;

	private String coverImagePath;

	private Integer pageSize;

	private ReadingDirection readingDirection;

	private List<? extends PhotoBookCommonEpisodeDetailResp> detailPages;

	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime lastModifiedAt;

	public PhotoBookEpisodeResp(PhotoBook photoBook) {
		this.coverImageUid = photoBook.getCoverImageUid();
		this.coverImagePath = photoBook.getImage().getImagePath();
		this.readingDirection = photoBook.getPage().getContent().getReadingDirection();
		this.photoBookUid = photoBook.getPhotoBookUid();
		this.title = photoBook.getTitle();
	}

}
