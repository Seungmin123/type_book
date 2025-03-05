package com.muzlive.kitpage.kitpage.service.transfer.kihno.dto.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SnsVideoInsertReq {

	@JsonProperty("action_code")
	private String actionCode;

	@JsonProperty("album_id")
	private String albumId;

	@JsonProperty("artist_name")
	private String artistName = "";

	@JsonProperty("video_list")
	private List<SnsUpsertVideoReq> videoList;

	@JsonProperty("user")
	private String user = "";

	@JsonProperty("folder_map")
	private Map<String, String> folderMap = new HashMap<>();

	@JsonProperty("bucket")
	private String bucket;

	public SnsVideoInsertReq(String actionCode, String albumId, String bucket) {
		this.actionCode = actionCode;
		this.albumId = albumId;
		this.bucket = bucket;
	}
}
