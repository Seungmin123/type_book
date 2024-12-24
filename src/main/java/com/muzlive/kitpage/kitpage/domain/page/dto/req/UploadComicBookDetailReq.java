package com.muzlive.kitpage.kitpage.domain.page.dto.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UploadComicBookDetailReq {

	@NotNull
	@JsonProperty("comicBookUid")
	private Long comicBookUid;

	@JsonProperty("episode")
	private String episode;

	@NotNull
	@JsonProperty("images")
	private List<MultipartFile> images;

}
