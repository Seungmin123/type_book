package com.muzlive.kitpage.kitpage.service.transfer.kittor.dto.resp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KittorOAuthLoginResp {

	private String accessToken;

	private String refreshToken;

	private String userType;

	private String userRole;

	private String email;

	private Boolean joined;
}
