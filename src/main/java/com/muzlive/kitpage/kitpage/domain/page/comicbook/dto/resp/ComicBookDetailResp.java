package com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp;

import com.muzlive.kitpage.kitpage.domain.page.Page;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.Video;
import com.muzlive.kitpage.kitpage.utils.enums.PageGenre;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComicBookDetailResp {

	private Long pageUid;

	private String contentId;

	private String title;

	private String writer;

	private String illustrator;

	private PageGenre genre;

	private List<ComicBookEpisodeResp> details;

	private List<Video> videos;

	private String infoText;

	public ComicBookDetailResp(Page page) {
		this.pageUid = page.getPageUid();
		this.contentId = page.getContentId();
		this.title = page.getTitle();
		this.genre = page.getGenre();
		this.infoText = page.getInfoText();
		this.writer = "";
		this.illustrator = "";
	}

}
