package com.muzlive.kitpage.kitpage.service.transfer.kittor.dto.req;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendVerificationReq {

	@NotNull
	private String email;

}
