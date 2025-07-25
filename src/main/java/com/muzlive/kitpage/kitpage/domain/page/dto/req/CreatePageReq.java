package com.muzlive.kitpage.kitpage.domain.page.dto.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.muzlive.kitpage.kitpage.utils.enums.PageContentType;
import com.muzlive.kitpage.kitpage.utils.enums.Region;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class CreatePageReq {

	@NotNull
	@JsonProperty("coverImage")
	private MultipartFile coverImage;

	@NotNull
	@JsonProperty("title")
	private String title;

	@JsonProperty("subtitle")
	private String subtitle;

	@NotNull
	@JsonProperty("infoText")
	private String infoText;

	@NotNull
	@JsonProperty("content_id")
	private String contentId;

	@JsonProperty("album_id")
	private String albumId;

}
