package com.muzlive.kitpage.kitpage.domain.page.photobook.dto.resp;

import com.muzlive.kitpage.kitpage.domain.page.photobook.PhotoBookDetail;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(name = "PhotoBookImageResp", description = "웹화보 상세 page 단위 이미지 타입 정보")
@Getter
@Setter
public class PhotoBookImageResp extends PhotoBookCommonEpisodeDetailResp {

	private Long photoBookDetailUid;

	private Long imageUid;

	// TODO 삭제 필요.
	@Deprecated
	private String saveFileName;

	private String md5;

	public PhotoBookImageResp(Long photoBookDetailUid, Integer page, Long imageUid, String saveFileName, String saveFilePath, String md5) {
		super(page, saveFilePath);
		this.photoBookDetailUid = photoBookDetailUid;
		this.imageUid = imageUid;
		this.saveFileName = saveFileName;
		this.md5 = md5;
	}

	public static PhotoBookImageResp of(PhotoBookDetail photoBookDetail) {
		return new PhotoBookImageResp(
			photoBookDetail.getPhotoBookDetailUid()
			, photoBookDetail.getPage()
			, photoBookDetail.getImage().getImageUid()
			, photoBookDetail.getImage().getSaveFileName()
			, photoBookDetail.getImage().getImagePath()
			, photoBookDetail.getImage().getMd5());
	}

}
