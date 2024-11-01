package com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp;

import com.muzlive.kitpage.kitpage.domain.page.Page;
import com.muzlive.kitpage.kitpage.utils.enums.KitStatus;
import com.muzlive.kitpage.kitpage.utils.enums.PageGenre;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

@Getter
@Setter
public class ComicBookResp {

	private String contentId;

	private Long pageUid;

	private Long coverImageUid;

	private String title;

	private String subtitle;

	private String writer;

	private String illustrator;

	private List<String> genreList;

	private KitStatus kitStatus;

	public ComicBookResp(Page page) {
		this.contentId = page.getContentId();
		this.pageUid = page.getPageUid();
		this.coverImageUid = page.getCoverImageUid();
		this.title = page.getTitle();
		this.subtitle = page.getSubTitle();
		if(CollectionUtils.isEmpty(page.getComicBooks())) {
			this.writer = "";
			this.illustrator = "";
			this.genreList = new ArrayList<>();
		}else {
			this.writer = page.getComicBooks().get(0).getWriter();
			this.illustrator = page.getComicBooks().get(0).getIllustrator();
			this.genreList = page.getComicBooks().get(0).getGenreList();
		}
	}
}
