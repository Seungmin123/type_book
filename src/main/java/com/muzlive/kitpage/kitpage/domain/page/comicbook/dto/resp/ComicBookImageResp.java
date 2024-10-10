package com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp;

import com.muzlive.kitpage.kitpage.domain.page.comicbook.ComicBookDetail;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ComicBookImageResp {

	private Long comicBookDetailUid;

	private String episode;

	private Integer page;

	private Long imageUid;

	private String saveFileName;

	private String md5;

	public static ComicBookImageResp of(ComicBookDetail comicBookDetail) {
		return new ComicBookImageResp(comicBookDetail.getComicBookDetailUid(), comicBookDetail.getEpisode(), comicBookDetail.getPage(),
			comicBookDetail.getImage().getImageUid(), comicBookDetail.getImage().getSaveFileName(), comicBookDetail.getImage().getMd5());
	}

}
