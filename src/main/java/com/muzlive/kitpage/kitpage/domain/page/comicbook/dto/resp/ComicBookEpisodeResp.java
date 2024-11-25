package com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.muzlive.kitpage.kitpage.config.serializer.LocalDateTimeSerializer;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.ComicBook;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComicBookEpisodeResp {

	private Long comicBookUid;

	private Long coverImageUid;

	private Integer volume;

	private String volumeUnit;

	private String pageUnit;

	private Integer pageSize;

	private List<ComicBookImageResp> detailPages;

	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime lastModifiedAt;

	public ComicBookEpisodeResp(ComicBook comicBook, String unit) {
		this.comicBookUid = comicBook.getComicBookUid();
		this.coverImageUid = comicBook.getCoverImageUid();
		this.volume = comicBook.getVolume();
		this.volumeUnit = comicBook.getVolumeUnit();
		this.pageUnit = comicBook.getPageUnit();
	}

}
