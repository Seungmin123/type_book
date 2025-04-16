package com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp;

import com.muzlive.kitpage.kitpage.domain.page.Content;
import com.muzlive.kitpage.kitpage.domain.page.dto.resp.CommonContentResp;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComicBookContentResp extends CommonContentResp {

	private List<ComicBookResp> comicBookResps;

	public ComicBookContentResp(Content content) {
		super(content);
	}
}
