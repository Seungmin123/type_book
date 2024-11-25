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
public class CreateContentReq {

	@NotNull
	@JsonProperty("contentType")
	private PageContentType contentType;

	@NotNull
	@JsonProperty("contentId")
	private String contentId;

	@NotNull
	@JsonProperty("region")
	private Region region;

	@NotNull
	@JsonProperty("infoText")
	private String infoText;

	@NotNull
	@JsonProperty("title")
	private String title;

	@NotNull
	@JsonProperty("image")
	private MultipartFile image;
}
