package com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp;

import com.muzlive.kitpage.kitpage.domain.page.Page;
import com.muzlive.kitpage.kitpage.utils.enums.KitStatus;
import com.muzlive.kitpage.kitpage.utils.enums.PageGenre;
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

	private PageGenre genre;

	private KitStatus kitStatus;

	public ComicBookResp(Page page) {
		this.contentId = page.getContentId();
		this.pageUid = page.getPageUid();
		this.coverImageUid = page.getCoverImageUid();
		this.title = page.getTitle();
		this.subtitle = page.getSubTitle();
		this.genre = page.getGenre();
		if(CollectionUtils.isEmpty(page.getComicBooks())) {
			this.writer = "";
			this.illustrator = "";
		}else {
			this.writer = page.getComicBooks().get(0).getWriter();
			this.illustrator = page.getComicBooks().get(0).getIllustrator();
		}
	}
}
