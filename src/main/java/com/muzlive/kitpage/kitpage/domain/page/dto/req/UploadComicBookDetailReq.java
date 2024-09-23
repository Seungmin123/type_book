package com.muzlive.kitpage.kitpage.domain.page.dto.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.muzlive.kitpage.kitpage.utils.enums.PageGenre;
import com.muzlive.kitpage.kitpage.utils.enums.Region;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UploadComicBookDetailReq {

	@NotNull
	@JsonProperty("contentId")
	private String contentId;

	@NotNull
	@JsonProperty("volume")
	private Integer volume;

	@JsonProperty("episode")
	private String episode;

	@NotNull
	@JsonProperty("images")
	private List<MultipartFile> images;

}
