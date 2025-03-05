package com.muzlive.kitpage.kitpage.service.transfer.kihno.dto.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SnsVideoReq {

	@JsonProperty("video_id")
	private String videoId;

	@JsonProperty("album_id")
	private String albumId;

	@Hidden
	@JsonProperty("token")
	private String token = "kit-page";

	@Hidden
	private String detailUrl;

	@Hidden
	private String vttUrl;
}
