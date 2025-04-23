package com.muzlive.kitpage.kitpage.domain.page.photobook.dto.resp;

import com.muzlive.kitpage.kitpage.domain.page.Page;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp.VideoResp;
import com.muzlive.kitpage.kitpage.domain.page.dto.resp.CommonContentDetailResp;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PhotoBookDetailResp extends CommonContentDetailResp {

	private List<PhotoBookEpisodeResp> details;

	private List<VideoResp> videos;

	public PhotoBookDetailResp(Page page) {
		super(page);
	}

}
