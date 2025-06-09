package com.muzlive.kitpage.kitpage.service.transfer.kittor.dto.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KittorWebUserJoinReq {

	@NotNull
	@JsonProperty("email")
	private String email;

	@NotNull
	@JsonProperty("password")
	private String password;

}
