package com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp;

import com.muzlive.kitpage.kitpage.domain.page.Page;
import com.muzlive.kitpage.kitpage.utils.enums.KitStatus;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

@Getter
@Setter
public class ComicBookResp {

	private Long pageUid;

	// TODO 삭제 필요
	private Long coverImageUid;

	private String coverImagePath;

	private String title;

	private String subtitle;

	private String writer;

	private String illustrator;

	private List<String> genreList;

	private Long totalSize;

	private KitStatus kitStatus;

	public ComicBookResp(Page page) {
		this.pageUid = page.getPageUid();
		this.coverImageUid = page.getCoverImageUid();
		this.coverImagePath = page.getImage().getImagePath();
		this.title = page.getTitle();
		this.subtitle = page.getSubTitle();
		this.totalSize = 0L;
		if(page.getContent() == null) {
			this.writer = "";
			this.illustrator = "";
			this.genreList = new ArrayList<>();
		}else {
			this.writer = page.getContent().getWriter();
			this.illustrator = page.getContent().getIllustrator();
			this.genreList = page.getContent().getGenreList();
		}
	}
}
