package com.muzlive.kitpage.kitpage.domain.page.dto.resp;

import com.muzlive.kitpage.kitpage.domain.page.Content;
import com.muzlive.kitpage.kitpage.utils.enums.PageContentType;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContentResp {

	private Long coverImageUid;

	private String coverImagePath;

	private String contentId;

	private PageContentType contentType;

	private String title;

	private String writer;

	private List<String> genreList;

	public ContentResp(Content content) {
		this.coverImageUid = content.getCoverImageUid();
		this.coverImagePath = content.getImage().getImagePath();
		this.contentId = content.getContentId();
		this.contentType = content.getContentType();
		this.title = content.getTitle();
		this.writer = content.getWriter();
		this.genreList = content.getGenreList();
	}


}
