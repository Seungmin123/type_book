package com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp;

import com.muzlive.kitpage.kitpage.domain.page.Page;
import com.muzlive.kitpage.kitpage.domain.page.dto.resp.CommonContentDetailResp;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComicBookDetailResp extends CommonContentDetailResp {

	private List<ComicBookEpisodeResp> details;

	private List<VideoResp> videos;

	public ComicBookDetailResp(Page page) {
		super(page);
	}

}
