package com.muzlive.kitpage.kitpage.domain.page.dto.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.muzlive.kitpage.kitpage.utils.enums.PageGenre;
import com.muzlive.kitpage.kitpage.utils.enums.Region;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UploadComicBookReq {

	@JsonProperty("coverImage")
	private MultipartFile coverImage;

	@NotNull
	@JsonProperty("writer")
	private String writer;

	@NotNull
	@JsonProperty("illustrator")
	private String illustrator;

	@NotNull
	@JsonProperty("title")
	private String title;

	@JsonProperty("subtitle")
	private String subtitle;

	@NotNull
	@JsonProperty("infoText")
	private String infoText;

	@NotNull
	@JsonProperty("company")
	private String company;

	@NotNull
	@JsonProperty("genre")
	private PageGenre genre;

	@NotNull
	@JsonProperty("region")
	private Region region;

}
