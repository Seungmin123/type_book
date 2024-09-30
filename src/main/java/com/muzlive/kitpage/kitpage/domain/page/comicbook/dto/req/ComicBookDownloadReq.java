package com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComicBookDownloadReq {

	@JsonProperty("pageUid")
	private Long pageUid;

	@JsonProperty("imageUid")
	private Long imageUid;

}
