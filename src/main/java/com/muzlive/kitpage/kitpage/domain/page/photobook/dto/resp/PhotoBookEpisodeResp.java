package com.muzlive.kitpage.kitpage.domain.page.photobook.dto.resp;

import com.muzlive.kitpage.kitpage.domain.page.dto.resp.CommonEpisodeResp;
import com.muzlive.kitpage.kitpage.domain.page.photobook.PhotoBook;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(name = "PhotoBookEpisodeResp", description = "웹화보 상세 volume 단위 정보")
@Getter
@Setter
public class PhotoBookEpisodeResp extends CommonEpisodeResp {

	private Long photoBookUid;

	private String title;

	public PhotoBookEpisodeResp(PhotoBook photoBook) {
		super(photoBook.getCoverImageUid(), photoBook.getImage().getImagePath(), photoBook.getPage().getContent().getReadingDirection());
		this.photoBookUid = photoBook.getPhotoBookUid();
		this.title = photoBook.getTitle();
	}

}
