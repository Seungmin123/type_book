package com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp;

import com.muzlive.kitpage.kitpage.domain.page.Page;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComicBookDetailResp {

	private Long pageUid;

	private String contentId;

	private String albumId;

	private List<ComicBookEpisodeResp> details;

	private List<VideoResp> videos;

	private String infoText;

	public ComicBookDetailResp(Page page) {
		this.pageUid = page.getPageUid();
		this.contentId = page.getContentId();
		this.albumId = page.getAlbumId();
		this.infoText = page.getInfoText();
	}

}
