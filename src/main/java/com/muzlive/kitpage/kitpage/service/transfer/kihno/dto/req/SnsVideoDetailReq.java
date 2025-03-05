package com.muzlive.kitpage.kitpage.service.transfer.kihno.dto.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SnsVideoDetailReq {

	@JsonProperty("token")
	private String token = "kit-page";

	@JsonProperty("video_id")
	private String videoId;

	@JsonProperty("quality_id")
	private String qualityId;

	@JsonProperty("height")
	private String height;

	@JsonProperty("file_type")
	private String fileType;

	public void setVideo_id(String videoId) {
		this.videoId = videoId;
	}

	public void setQuality_id(String qualityId) {
		this.qualityId = qualityId;
	}

	public void setFile_type(String fileType) {
		this.fileType = fileType;
	}
}
