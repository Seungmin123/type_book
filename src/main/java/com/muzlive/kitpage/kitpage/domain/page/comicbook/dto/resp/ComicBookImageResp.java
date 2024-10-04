package com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp;

import com.muzlive.kitpage.kitpage.domain.page.comicbook.ComicBookDetail;
import java.util.ArrayList;
import java.util.List;
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

	public static ComicBookImageResp of(ComicBookDetail comicBookDetail) {
		return new ComicBookImageResp(comicBookDetail.getComicBookDetailUid(), comicBookDetail.getEpisode(), comicBookDetail.getPage(),
			comicBookDetail.getImage().getImageUid(), comicBookDetail.getImage().getSaveFileName());
	}

	public static List<ComicBookImageResp> of(List<ComicBookDetail> comicBookDetails) {
		List<ComicBookImageResp> resps = new ArrayList<>();
		for(ComicBookDetail comicBookDetail : comicBookDetails) {
			resps.add(ComicBookImageResp.of(comicBookDetail));
		}
		return resps;
	}

}
