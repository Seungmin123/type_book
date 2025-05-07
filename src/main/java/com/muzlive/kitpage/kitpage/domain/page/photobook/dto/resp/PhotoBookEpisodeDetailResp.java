package com.muzlive.kitpage.kitpage.domain.page.photobook.dto.resp;

import com.muzlive.kitpage.kitpage.domain.page.dto.resp.CommonEpisodeDetailResp;
import com.muzlive.kitpage.kitpage.domain.page.photobook.PhotoBookDetail;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(name = "PhotoBookEpisodeDetailResp", description = "웹화보 상세 page 단위 PDF 타입 정보")
@Getter
@Setter
public class PhotoBookEpisodeDetailResp extends CommonEpisodeDetailResp {

	private Long photoBookDetailUid;

	private Long pdfUid;

	private String md5;

	public PhotoBookEpisodeDetailResp(Integer page, String saveFilePath, Long photoBookDetailUid, Long pdfUid, String md5) {
		super(page, saveFilePath);
		this.photoBookDetailUid = photoBookDetailUid;
		this.pdfUid = pdfUid;
		this.md5 = md5;
	}

	public static PhotoBookEpisodeDetailResp of(PhotoBookDetail photoBookDetail) {
		return new PhotoBookEpisodeDetailResp(
			photoBookDetail.getPage(),
			photoBookDetail.getPdf().getPdfPath(),
			photoBookDetail.getPhotoBookDetailUid(),
			photoBookDetail.getPdfUid(),
			photoBookDetail.getPdf().getMd5()
		);
	}
}
