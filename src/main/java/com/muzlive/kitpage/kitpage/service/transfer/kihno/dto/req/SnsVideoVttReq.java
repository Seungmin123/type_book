package com.muzlive.kitpage.kitpage.service.transfer.kihno.dto.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SnsVideoVttReq {

	@JsonProperty("token")
	private String token = "kit-page-token";

	@JsonProperty("kit_id")
	private String kitId = "kit-page-kit-id";

	@JsonProperty("video_id")
	private String videoId;

	@JsonProperty("country_code")
	private String countryCode;

	public void setVideo_id(String videoId) {
		this.videoId = videoId;
	}

	public void setCountry_code(String countryCode) {
		this.countryCode = countryCode;
	}

}
