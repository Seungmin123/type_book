package com.muzlive.kitpage.kitpage.domain.page.photobook.dto.resp;

import com.muzlive.kitpage.kitpage.domain.page.Page;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp.VideoResp;
import com.muzlive.kitpage.kitpage.domain.page.dto.resp.CommonContentDetailResp;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Schema(name = "PhotoBookDetailResp", description = "웹화보 상세 정보")
@Getter
@Setter
public class PhotoBookDetailResp extends CommonContentDetailResp {

	private List<PhotoBookEpisodeResp> details;

	private List<VideoResp> videos;

	public PhotoBookDetailResp(Page page) {
		super(page);
	}

}
