package com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp;

import com.muzlive.kitpage.kitpage.domain.page.comicbook.ComicBookDetail;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(name = "ComicBookImageResp", description = "만화책 상세 page 단위 이미지 타입 정보")
@Getter
@Setter
public class ComicBookImageResp {

	private Long comicBookDetailUid;

	private String episode;

	private Long imageUid;

	private String md5;

	private Integer page;

	private String saveFilePath;

	@Deprecated
	private String saveFileName;

	public ComicBookImageResp(Integer page, String saveFilePath, Long comicBookDetailUid, Long imageUid, String episode, String md5, String saveFileName) {
		this.page = page;
		this.saveFilePath = saveFilePath;
		this.comicBookDetailUid = comicBookDetailUid;
		this.imageUid = imageUid;
		this.episode = episode;
		this.md5 = md5;
		this.saveFileName = saveFileName;
	}

	public static ComicBookImageResp of(ComicBookDetail comicBookDetail) {
		return new ComicBookImageResp(
			comicBookDetail.getPage(),
			comicBookDetail.getImage().getImagePath(),
			comicBookDetail.getComicBookDetailUid(),
			comicBookDetail.getImage().getImageUid(),
			comicBookDetail.getEpisode(),
			comicBookDetail.getImage().getMd5(),
			comicBookDetail.getImage().getSaveFileName()
		);
	}

}
