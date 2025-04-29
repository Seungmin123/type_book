package com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp;

import com.muzlive.kitpage.kitpage.domain.page.comicbook.ComicBookDetail;
import com.muzlive.kitpage.kitpage.domain.page.dto.resp.CommonEpisodeDetailResp;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComicBookImageResp extends CommonEpisodeDetailResp {

	private Long comicBookDetailUid;

	private String episode;

	private Long imageUid;

	private String md5;

	public ComicBookImageResp(Integer page, String saveFilePath, Long comicBookDetailUid, Long imageUid, String episode, String md5) {
		super(page, saveFilePath);
		this.comicBookDetailUid = comicBookDetailUid;
		this.imageUid = imageUid;
		this.episode = episode;
		this.md5 = md5;
	}

	public static ComicBookImageResp of(ComicBookDetail comicBookDetail) {
		return new ComicBookImageResp(
			comicBookDetail.getPage(),
			comicBookDetail.getImage().getImagePath(),
			comicBookDetail.getComicBookDetailUid(),
			comicBookDetail.getImage().getImageUid(),
			comicBookDetail.getEpisode(),
			comicBookDetail.getImage().getMd5());
	}

}
