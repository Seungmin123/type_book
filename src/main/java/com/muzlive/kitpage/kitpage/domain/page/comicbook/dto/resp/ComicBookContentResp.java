package com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp;

import com.muzlive.kitpage.kitpage.domain.page.Content;
import com.muzlive.kitpage.kitpage.domain.page.dto.resp.CommonContentResp;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Schema(name = "ComicBookContentResp", description = "만화책 리스트 정보")
@Getter
@Setter
public class ComicBookContentResp extends CommonContentResp {

	private List<ComicBookResp> comicBookResps;

	public ComicBookContentResp(Content content) {
		super(content);
	}
}
