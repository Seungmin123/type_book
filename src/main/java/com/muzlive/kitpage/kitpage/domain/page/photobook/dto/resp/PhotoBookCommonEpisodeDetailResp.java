package com.muzlive.kitpage.kitpage.domain.page.photobook.dto.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Schema(
	description = "컨텐츠 상세 page 단위 응답 클래스",
	oneOf = {
		PhotoBookImageResp.class,
		PhotoBookEpisodeDetailResp.class
	}
)
@AllArgsConstructor
@Getter
@Setter
public class PhotoBookCommonEpisodeDetailResp {

	private Integer page;

	private String saveFilePath;

}
