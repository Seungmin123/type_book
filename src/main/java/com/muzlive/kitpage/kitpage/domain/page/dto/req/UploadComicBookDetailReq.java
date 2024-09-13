package com.muzlive.kitpage.kitpage.domain.page.dto.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.muzlive.kitpage.kitpage.utils.enums.PageGenre;
import com.muzlive.kitpage.kitpage.utils.enums.Region;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UploadComicBookDetailReq {

	@JsonProperty("contentId")
	private String contentId;

	@JsonProperty("title")
	private String title;

	@JsonProperty("chapter")
	private String chapter;

	@JsonProperty("page")
	private Integer page;

	@JsonProperty("images")
	private List<MultipartFile> images;

	@JsonProperty("region")
	private Region region;

}
