package com.muzlive.kitpage.kitpage.service.page;

import com.muzlive.kitpage.kitpage.domain.page.comicbook.ComicBook;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.ComicBookDetail;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.repository.ComicBookBookmarkRepository;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.repository.ComicBookDetailRepository;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.repository.ComicBookLogRepository;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.repository.ComicBookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ComicService {

	private final ComicBookRepository comicBookRepository;

	private final ComicBookDetailRepository comicBookDetailRepository;

	private final ComicBookLogRepository comicBookLogRepository;

	private final ComicBookBookmarkRepository comicBookBookmarkRepository;

	public ComicBook upsertComicBook(ComicBook comicBook) throws Exception {
		return comicBookRepository.save(comicBook);
	}

	/**
	 * Page 자동 증가 Insert
	 * @param comicBookDetail
	 * @return
	 * @throws Exception
	 */
	public ComicBookDetail createComicBookDetail(ComicBookDetail comicBookDetail) throws Exception {
		comicBookDetail.setPage(
			comicBookDetailRepository.findMaxPage(comicBookDetail.getComicBookUid(), comicBookDetail.getChapter())
		);

		return comicBookDetailRepository.save(comicBookDetail);
	}
}
