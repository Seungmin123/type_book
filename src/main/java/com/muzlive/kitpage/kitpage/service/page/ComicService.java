package com.muzlive.kitpage.kitpage.service.page;

import static com.muzlive.kitpage.kitpage.domain.page.comicbook.QComicBook.comicBook;
import static com.muzlive.kitpage.kitpage.domain.user.QKit.kit;

import com.muzlive.kitpage.kitpage.domain.page.comicbook.ComicBook;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.ComicBookDetail;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.repository.ComicBookBookmarkRepository;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.repository.ComicBookDetailRepository;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.repository.ComicBookLogRepository;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.repository.ComicBookRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@RequiredArgsConstructor
@Service
public class ComicService {

	private final JPAQueryFactory queryFactory;

	private final ComicBookRepository comicBookRepository;

	private final ComicBookDetailRepository comicBookDetailRepository;

	private final ComicBookLogRepository comicBookLogRepository;

	private final ComicBookBookmarkRepository comicBookBookmarkRepository;

	public ComicBook upsertComicBook(ComicBook comicBook) throws Exception {
		return comicBookRepository.save(comicBook);
	}

	public ComicBookDetail upsertComicBookDetail(ComicBookDetail comicBookDetail) throws Exception {
		return comicBookDetailRepository.save(comicBookDetail);
	}

	public int findComicBookMaxPage(Long comicBookUid, Integer volume) throws Exception {
		Integer page = comicBookDetailRepository.findMaxPage(comicBookUid, volume);
		return page == null ? 0 : page;
	}

	public List<ComicBook> getComicBooksByDeviceId(String deviceId) throws Exception {

		List<ComicBook> comicBooks = queryFactory
			.selectFrom(comicBook)
			.innerJoin(kit).on(kit.pageUid.eq(comicBook.page.pageUid))
			.where(kit.deviceId.eq(deviceId))
			.fetch();

		if(CollectionUtils.isEmpty(comicBooks)) comicBooks = new ArrayList<>();

		return comicBooks;
	}
}
