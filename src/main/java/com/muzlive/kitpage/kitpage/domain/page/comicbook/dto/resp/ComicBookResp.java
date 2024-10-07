package com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp;

import com.muzlive.kitpage.kitpage.domain.page.Page;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.Video;
import com.muzlive.kitpage.kitpage.utils.enums.KitStatus;
import com.muzlive.kitpage.kitpage.utils.enums.PageGenre;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

@Getter
@Setter
public class ComicBookResp {

	private Long pageUid;

	private String contentId;

	private String title;

	private String subtitle;

	private String writer;

	private String illustrator;

	private PageGenre genre;

	private List<ComicBookEpisodeResp> details;

	private List<Video> videos;

	private String infoText;

	private KitStatus kitStatus;

	public ComicBookResp(Page page) {
		this.pageUid = page.getPageUid();
		this.contentId = page.getContentId();
		this.title = page.getTitle();
		this.subtitle = page.getSubTitle();
		this.genre = page.getGenre();
		this.infoText = page.getInfoText();
		if(CollectionUtils.isEmpty(page.getComicBooks())) {
			this.writer = "";
			this.illustrator = "";
		}else {
			this.writer = page.getComicBooks().get(0).getWriter();
			this.illustrator = page.getComicBooks().get(0).getIllustrator();
		}
	}
}
