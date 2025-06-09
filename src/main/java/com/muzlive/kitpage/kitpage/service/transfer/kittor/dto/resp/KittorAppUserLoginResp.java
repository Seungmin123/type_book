package com.muzlive.kitpage.kitpage.service.transfer.kittor.dto.resp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KittorAppUserLoginResp {

	private String accessToken;

	private String refreshToken;

	private String email;

	private String password;

	private Long userId;

	private String userRole;

	private String userType;
}
