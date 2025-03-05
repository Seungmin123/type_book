package com.muzlive.kitpage.kitpage.domain.page.dto.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.muzlive.kitpage.kitpage.utils.enums.VideoCode;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UploadVideoReq {

	@NotNull
	@JsonProperty("contentId")
	private String contentId;

	@NotNull
	@JsonProperty("pageUid")
	private Long pageUid;

	@JsonProperty("album")
	private String album;

	@JsonProperty("duration")
	private String duration;

	@JsonProperty("title")
	private String title;

	@JsonProperty("file")
	private MultipartFile file;

	@JsonProperty("image")
	private MultipartFile image;

	@JsonProperty("streamUrl")
	private String streamUrl;

	@JsonProperty("videoId")
	private String videoId;

	@JsonProperty("videoFilePath")
	private String videoFilePath;

	@JsonProperty("videoThumbnailPath")
	private String videoThumbnailPath;

	@JsonProperty("subTitlePath")
	private String subTitlePath;

	@JsonProperty("languageCode")
	private String languageCode;

	@JsonProperty("videoCode")
	private VideoCode videoCode;

}
