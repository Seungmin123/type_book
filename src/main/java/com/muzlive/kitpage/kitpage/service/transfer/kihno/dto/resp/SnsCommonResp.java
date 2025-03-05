package com.muzlive.kitpage.kitpage.service.transfer.kihno.dto.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SnsCommonResp<T>{

	@JsonProperty("data")
	private T data;

	@JsonProperty("status_code")
	private Integer statusCode;

	@JsonProperty("error_msg")
	private String errorMsg;
}
