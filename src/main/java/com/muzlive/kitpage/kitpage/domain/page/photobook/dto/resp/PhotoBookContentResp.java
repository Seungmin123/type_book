package com.muzlive.kitpage.kitpage.domain.page.photobook.dto.resp;

import com.muzlive.kitpage.kitpage.domain.page.Content;
import com.muzlive.kitpage.kitpage.domain.page.dto.resp.CommonContentResp;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Schema(name = "PhotoBookContentResp", description = "웹화보 리스트 정보")
@Getter
@Setter
public class PhotoBookContentResp extends CommonContentResp {

	private List<PhotoBookResp> photoBookResps;

	public PhotoBookContentResp(Content content) {
		super(content);
	}
}
