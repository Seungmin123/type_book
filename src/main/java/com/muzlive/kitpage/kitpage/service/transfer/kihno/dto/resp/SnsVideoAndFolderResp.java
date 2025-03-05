package com.muzlive.kitpage.kitpage.service.transfer.kihno.dto.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SnsVideoAndFolderResp{

	@JsonProperty("folder_name")
	private String folderName;

	@JsonProperty("video_ids")
	private String videoId;

}
