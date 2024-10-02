package com.muzlive.kitpage.kitpage.service.transfer.kittor.dto.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KittorChangePasswordReq {

	@NotNull
	@JsonProperty("oldPassword")
	private String oldPassword;

	@NotNull
	@JsonProperty("newPassword")
	private String newPassword;

}
