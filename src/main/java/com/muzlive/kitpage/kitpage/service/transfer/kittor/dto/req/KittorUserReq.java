package com.muzlive.kitpage.kitpage.service.transfer.kittor.dto.req;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KittorUserReq {

	@NotNull
	private String email;

	@NotNull
	private String password;

}
