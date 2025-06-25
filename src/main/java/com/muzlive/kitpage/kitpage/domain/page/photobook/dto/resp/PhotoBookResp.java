package com.muzlive.kitpage.kitpage.domain.page.photobook.dto.resp;

import com.muzlive.kitpage.kitpage.domain.page.Page;
import com.muzlive.kitpage.kitpage.utils.enums.KitStatus;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PhotoBookResp {

	private Long pageUid;

	private Long coverImageUid;

	private String coverImagePath;

	private String title;

	private String subtitle;

	private String writer;

	private List<String> genreList;

	private Long totalSize;

	private KitStatus kitStatus;

	public PhotoBookResp(Page page) {
		this.pageUid = page.getPageUid();
		this.coverImageUid = page.getCoverImageUid();
		this.coverImagePath = page.getImage().getImagePath();
		this.title = page.getTitle();
		this.subtitle = page.getSubTitle();
		this.totalSize = 0L;
		if(page.getContent() == null) {
			this.writer = "";
			this.genreList = new ArrayList<>();
		}else {
			this.writer = page.getContent().getWriter();
			this.genreList = page.getContent().getGenreList();
		}
	}
}
