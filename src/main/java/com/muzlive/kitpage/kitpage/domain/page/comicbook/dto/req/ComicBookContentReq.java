package com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComicBookContentReq {

	@NotNull
	@JsonProperty("pageUid")
	private Long pageUid;

	@NotNull
	@JsonProperty("contentId")
	private String contentId;

	@NotNull
	@JsonProperty("deviceId")
	private String deviceId;

}
