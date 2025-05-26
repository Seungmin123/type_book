package com.muzlive.kitpage.kitpage.service.transfer.kittor.dto.req;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KittorPreSginedFileReq {

	@Hidden
	private String type = "profile_account";

	private String extension;

}
