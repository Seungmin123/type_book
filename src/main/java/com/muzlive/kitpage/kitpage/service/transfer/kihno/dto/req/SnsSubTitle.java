package com.muzlive.kitpage.kitpage.service.transfer.kihno.dto.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SnsSubTitle {

	@JsonProperty("subtitle_path")
	private String subTitlePath;

	@JsonProperty("language_code")
	private String languageCode;

}
