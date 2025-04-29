package com.muzlive.kitpage.kitpage.domain.page.photobook.dto.resp;

import com.muzlive.kitpage.kitpage.domain.page.dto.resp.CommonEpisodeResp;
import com.muzlive.kitpage.kitpage.domain.page.photobook.PhotoBook;
import lombok.Getter;
import lombok.Setter;

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
