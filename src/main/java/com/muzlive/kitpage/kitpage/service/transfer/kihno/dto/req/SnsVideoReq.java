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

	@JsonProperty("token")
	private String token = "kit-page";

	@JsonProperty("detail_url")
	private String detailUrl;
	@Hidden
	@JsonProperty("vtt_url")
	private String vttUrl;

	public SnsVideoReq(String videoId, String albumId, String detailUrl, String vttUrl) {
		this.videoId = videoId;
		this.albumId = albumId;
		this.detailUrl = detailUrl;
		this.vttUrl = vttUrl;
	}
}
