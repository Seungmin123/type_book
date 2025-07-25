package com.muzlive.kitpage.kitpage.domain.user.dto.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckTagReq {

	@NotNull
	@JsonProperty("serialNumber")
	private String serialNumber;

}
