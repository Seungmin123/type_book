package com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.muzlive.kitpage.kitpage.config.serializer.LocalDateTimeSerializer;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.ComicBook;

import com.muzlive.kitpage.kitpage.utils.enums.ReadingDirection;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Schema(name = "ComicBookEpisodeResp", description = "만화책 상세 volume 단위 정보")
@Getter
@Setter
public class ComicBookEpisodeResp {

	private Long comicBookUid;

	private Integer volume;

	private String volumeUnit;

	private String pageUnit;

	private Long coverImageUid;

	private String coverImagePath;

	private Integer pageSize;

	private ReadingDirection readingDirection;

	private List<ComicBookImageResp> detailPages;

	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime lastModifiedAt;

	public ComicBookEpisodeResp(ComicBook comicBook) {
		this.coverImageUid = comicBook.getCoverImageUid();
		this.coverImagePath = comicBook.getImage().getImagePath();
		this.readingDirection = comicBook.getPage().getContent().getReadingDirection();
		this.comicBookUid = comicBook.getComicBookUid();
		this.volume = comicBook.getVolume();
		this.volumeUnit = comicBook.getPage().getContent().getVolumeUnit();
		this.pageUnit = comicBook.getPage().getContent().getPageUnit();
	}

}
