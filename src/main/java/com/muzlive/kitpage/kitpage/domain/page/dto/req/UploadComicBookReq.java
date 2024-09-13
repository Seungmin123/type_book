package com.muzlive.kitpage.kitpage.domain.page.dto.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.muzlive.kitpage.kitpage.utils.enums.PageGenre;
import com.muzlive.kitpage.kitpage.utils.enums.Region;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UploadComicBookReq {

	@JsonProperty("contentId")
	private String contentId;

	@JsonProperty("coverImage")
	private MultipartFile coverImage;

	@JsonProperty("writer")
	private String writer;

	@JsonProperty("illustrator")
	private String illustrator;

	@JsonProperty("title")
	private String title;

	@JsonProperty("subtitle")
	private String subtitle;

	@JsonProperty("infoText")
	private String infoText;

	@JsonProperty("company")
	private String company;

	@JsonProperty("genre")
	private PageGenre genre;

	@JsonProperty("region")
	private Region region;

}
