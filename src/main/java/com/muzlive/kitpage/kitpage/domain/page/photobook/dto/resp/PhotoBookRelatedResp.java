package com.muzlive.kitpage.kitpage.domain.page.photobook.dto.resp;

import com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp.ComicBookResp;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PhotoBookRelatedResp {

	private List<ComicBookResp> comicBookResps;

	private ComicBookResp taggedComicBook;

}
