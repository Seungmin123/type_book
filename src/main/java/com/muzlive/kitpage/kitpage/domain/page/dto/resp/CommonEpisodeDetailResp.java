package com.muzlive.kitpage.kitpage.domain.page.dto.resp;

import com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp.ComicBookImageResp;
import com.muzlive.kitpage.kitpage.domain.page.photobook.dto.resp.PhotoBookEpisodeDetailResp;
import com.muzlive.kitpage.kitpage.domain.page.photobook.dto.resp.PhotoBookImageResp;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

// TODO 형태에 너무 종속적이게 됨. 제거 필요
@Schema(
	description = "컨텐츠 상세 page 단위 응답 클래스",
	oneOf = {
		ComicBookImageResp.class,
		PhotoBookImageResp.class,
		PhotoBookEpisodeDetailResp.class
	}
)
@AllArgsConstructor
@Getter
@Setter
public class CommonEpisodeDetailResp {

	private Integer page;

	private String saveFilePath;

}
