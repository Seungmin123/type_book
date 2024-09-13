package com.muzlive.kitpage.kitpage.domain.page.dto.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UploadMusicReq {

	@JsonProperty("contentId")
	private String contentId;

	@JsonProperty("album")
	private String album;

	@JsonProperty("artist")
	private String artist;

	@JsonProperty("title")
	private String title;

	@JsonProperty("file")
	private MultipartFile file;

	@JsonProperty("image")
	private MultipartFile image;

}
