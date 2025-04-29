package com.muzlive.kitpage.kitpage.domain.page.photobook.dto.resp;

import com.muzlive.kitpage.kitpage.domain.page.dto.resp.CommonEpisodeDetailResp;
import com.muzlive.kitpage.kitpage.domain.page.photobook.PhotoBookDetail;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PhotoBookEpisodeDetailResp extends CommonEpisodeDetailResp {

	private Long photoBookDetailUid;

	public PhotoBookEpisodeDetailResp(Integer page, String saveFilePath, Long photoBookDetailUid) {
		super(page, saveFilePath);
		this.photoBookDetailUid = photoBookDetailUid;
	}

	public static PhotoBookEpisodeDetailResp of(PhotoBookDetail photoBookDetail) {
		return new PhotoBookEpisodeDetailResp(
			photoBookDetail.getPage(),
			photoBookDetail.getPdf().getPdfPath(),
			photoBookDetail.getPhotoBookDetailUid()
		);
	}
}
