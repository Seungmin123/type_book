package com.muzlive.kitpage.kitpage.domain.page.photobook.dto.resp;

import com.muzlive.kitpage.kitpage.domain.page.Content;
import com.muzlive.kitpage.kitpage.domain.page.dto.resp.CommonContentResp;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PhotoBookContentResp extends CommonContentResp {

	private List<PhotoBookResp> photoBookResps;

	public PhotoBookContentResp(Content content) {
		super(content);
	}
}
