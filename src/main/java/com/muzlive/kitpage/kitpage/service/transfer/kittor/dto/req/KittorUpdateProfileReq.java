package com.muzlive.kitpage.kitpage.service.transfer.kittor.dto.req;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KittorUpdateProfileReq {

	private String nickname;

	private String profileFileUrl;

	private String profileFileName;

	private boolean resetProfile = false;

}
