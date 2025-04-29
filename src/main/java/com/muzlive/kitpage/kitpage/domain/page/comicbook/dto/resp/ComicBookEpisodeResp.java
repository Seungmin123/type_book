package com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.muzlive.kitpage.kitpage.config.serializer.LocalDateTimeSerializer;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.ComicBook;
import com.muzlive.kitpage.kitpage.domain.page.dto.resp.CommonEpisodeResp;
import com.muzlive.kitpage.kitpage.utils.enums.ReadingDirection;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComicBookEpisodeResp extends CommonEpisodeResp {

	private Long comicBookUid;

	private Integer volume;

	private String volumeUnit;

	private String pageUnit;

	public ComicBookEpisodeResp(ComicBook comicBook) {
		super(comicBook.getCoverImageUid(), comicBook.getImage().getImagePath(), comicBook.getPage().getContent().getReadingDirection());
		this.comicBookUid = comicBook.getComicBookUid();
		this.volume = comicBook.getVolume();
		this.volumeUnit = comicBook.getPage().getContent().getVolumeUnit();
		this.pageUnit = comicBook.getPage().getContent().getPageUnit();
	}

}
