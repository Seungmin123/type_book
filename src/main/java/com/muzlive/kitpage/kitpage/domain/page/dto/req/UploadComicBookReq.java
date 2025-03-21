package com.muzlive.kitpage.kitpage.domain.page.dto.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UploadComicBookReq {

	@NotNull
	@JsonProperty("pageUid")
	private Long pageUid;

	@NotNull
	@JsonProperty("coverImage")
	private MultipartFile coverImage;

	@NotNull
	@JsonProperty("volume")
	private Integer volume;

}
