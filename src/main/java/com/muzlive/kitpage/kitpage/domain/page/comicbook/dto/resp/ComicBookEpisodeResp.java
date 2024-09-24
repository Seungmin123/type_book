package com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp;

import com.muzlive.kitpage.kitpage.domain.page.comicbook.ComicBook;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComicBookEpisodeResp {

	private Long comicBookUid;

	private Long coverImageUid;

	private Integer volume;

	private String unit;

	private Integer pageSize;

	public ComicBookEpisodeResp(ComicBook comicBook, String unit) {
		this.comicBookUid = comicBook.getComicBookUid();
		this.coverImageUid = comicBook.getCoverImageUid();
		this.volume = comicBook.getVolume();
		this.unit = unit;
		this.pageSize = (comicBook.getComicBookDetails().isEmpty()) ? 0 : comicBook.getComicBookDetails().size();
	}

}
