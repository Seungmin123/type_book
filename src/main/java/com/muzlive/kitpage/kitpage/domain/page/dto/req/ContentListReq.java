package com.muzlive.kitpage.kitpage.domain.page.dto.req;

import com.muzlive.kitpage.kitpage.domain.common.dto.req.PageReq;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContentListReq extends PageReq {

	private boolean isDescending = true;

	private Long contentUid;

	@Hidden
	private String deviceId;
}
