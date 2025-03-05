package com.muzlive.kitpage.kitpage.service.transfer.kihno.dto.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SnsUpsertVideoReq {

	@JsonProperty("video_type")
	private String videoType = "VIDEO";

	@JsonProperty("video_id")
	private String videoId;

	@JsonProperty("video_file_path")
	private String videoFilePath;

	@JsonProperty("video_thumbnail_path")
	private String videoThumbnailPath;

	@JsonProperty("subtitle_list")
	private List<SnsSubTitle> subTitleList;

	@JsonProperty("video_title")
	private String videoTitle;

	@JsonProperty("duration")
	private Integer duration;

	@JsonProperty("new_video_id")
	private String newVideoId = "";

	@JsonProperty("folder_name")
	private String folderName = "";

	@JsonProperty("parents")
	private String parents = "";

	@JsonProperty("member_name")
	private String memberName;

	@JsonProperty("member_thumbnail")
	private String memberThumbnail = "";

	@JsonProperty("track_num")
	private Integer trackNum = 1;

	@JsonProperty("onlykitYN")
	private String onlyKitYn;

	@JsonProperty("pc_encodingYN")
	private String pcEncodingYn;

	@JsonProperty("landscapeYN")
	private String landScapeYn;

	@JsonProperty("only_vtt")
	private Boolean onlyVtt;

	public SnsUpsertVideoReq(String videoFilePath, String videoThumbnailPath, String videoTitle, Integer duration) {
		this.videoFilePath = videoFilePath;
		this.videoThumbnailPath = videoThumbnailPath;
		this.videoTitle = videoTitle;
		this.duration = duration;
	}
}
