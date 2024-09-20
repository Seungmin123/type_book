package com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp;

import com.muzlive.kitpage.kitpage.domain.page.comicbook.ComicBook;
import com.muzlive.kitpage.kitpage.utils.enums.PageGenre;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class ComicBookResp {

	private Long comicBookUid;

	private String contentId;

	private Long coverImageUid;

	private String writer;

	private String illustrator;

	private String title;

	private String subTitle;

	private String infoText;

	private String company;

	private PageGenre genre;

	private Double rate;

	public static ComicBookResp of(ComicBook comicBook) {
		return ComicBookResp.builder()
			.comicBookUid(comicBook.getComicBookUid())
			.contentId(comicBook.getPage().getContentId())
			.coverImageUid(comicBook.getPage().getCoverImageUid())
			.writer(comicBook.getWriter())
			.illustrator(comicBook.getIllustrator())
			.title(comicBook.getPage().getTitle())
			.subTitle(comicBook.getPage().getSubTitle())
			.infoText(comicBook.getPage().getInfoText())
			.company(comicBook.getPage().getCompany())
			.genre(comicBook.getPage().getGenre())
			.rate(comicBook.getPage().getRate())
			.build();
	}

	public static List<ComicBookResp> of(List<ComicBook> comicBooks) {
		List<ComicBookResp> list = new ArrayList<>();

		for(ComicBook comicBook : comicBooks) {
			list.add(ComicBookResp.of(comicBook));
		}

		return list;
	}
}
