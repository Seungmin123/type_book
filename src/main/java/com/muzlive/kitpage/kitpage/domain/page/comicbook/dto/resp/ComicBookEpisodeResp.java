package com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp;

import com.muzlive.kitpage.kitpage.domain.page.comicbook.ComicBook;
import com.muzlive.kitpage.kitpage.domain.page.dto.resp.CommonEpisodeResp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(name = "ComicBookEpisodeResp", description = "만화책 상세 volume 단위 정보")
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
