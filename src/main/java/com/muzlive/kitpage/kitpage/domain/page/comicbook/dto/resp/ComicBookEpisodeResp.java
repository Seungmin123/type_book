package com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp;

import com.muzlive.kitpage.kitpage.domain.page.comicbook.ComicBook;
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

	private String unit;

	private Integer pageSize;

	private List<ComicBookImageResp> detailPages;

	public ComicBookEpisodeResp(ComicBook comicBook, String unit) {
		this.comicBookUid = comicBook.getComicBookUid();
		this.coverImageUid = comicBook.getCoverImageUid();
		this.volume = comicBook.getVolume();
		this.unit = unit;
		if(comicBook.getComicBookDetails().isEmpty()){
			this.pageSize = 0;
			this.detailPages = new ArrayList<>();
		} else {
			this.pageSize = comicBook.getComicBookDetails().size();
			this.detailPages = ComicBookImageResp.of(comicBook.getComicBookDetails());
		}
	}

}
