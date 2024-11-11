package com.muzlive.kitpage.kitpage.domain.page.dto.req;

import com.fasterxml.jackson.annotation.JsonProperty;
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

}
