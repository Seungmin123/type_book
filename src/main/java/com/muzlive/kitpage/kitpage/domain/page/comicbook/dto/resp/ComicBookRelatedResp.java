package com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComicBookRelatedResp {

	private List<ComicBookResp> comicBookResps;

	private ComicBookResp taggedComicBook;

}
