package com.muzlive.kitpage.kitpage.domain.page.photobook.dto.resp;

import com.muzlive.kitpage.kitpage.domain.page.photobook.PhotoBookDetail;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class PhotoBookImageResp {

	private Long photoBookDetailUid;

	private Integer page;

	private Long imageUid;

	private String saveFileName;

	private String md5;

	public static PhotoBookImageResp of(PhotoBookDetail photoBookDetail) {
		return new PhotoBookImageResp(photoBookDetail.getPhotoBookDetailUid(), photoBookDetail.getPage(),
			photoBookDetail.getImage().getImageUid(), photoBookDetail.getImage().getSaveFileName(), photoBookDetail.getImage().getMd5());
	}

}
