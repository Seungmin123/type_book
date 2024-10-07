package com.muzlive.kitpage.kitpage.service.page;

import static com.muzlive.kitpage.kitpage.domain.page.comicbook.QComicBook.comicBook;
import static com.muzlive.kitpage.kitpage.domain.user.QKit.kit;

import com.muzlive.kitpage.kitpage.config.exception.CommonException;
import com.muzlive.kitpage.kitpage.config.exception.ExceptionCode;
import com.muzlive.kitpage.kitpage.domain.page.Page;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.ComicBook;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.ComicBookDetail;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp.ComicBookDetailResp;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp.ComicBookEpisodeResp;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.repository.ComicBookDetailRepository;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.repository.ComicBookRepository;
import com.muzlive.kitpage.kitpage.domain.user.KitLog;
import com.muzlive.kitpage.kitpage.utils.constants.ApplicationConstants;
import com.muzlive.kitpage.kitpage.utils.enums.KitStatus;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
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

	public ComicBook getComicBook(Long comicBookUid) throws Exception {
		return comicBookRepository.findById(comicBookUid)
			.orElseThrow(() -> new CommonException(ExceptionCode.CANNOT_FIND_ITEM_THAT_MATCH_THE_PARAM));
	}

	public ComicBook upsertComicBook(ComicBook comicBook) throws Exception {
		return comicBookRepository.save(comicBook);
	}

	public ComicBookDetail upsertComicBookDetail(ComicBookDetail comicBookDetail) throws Exception {
		return comicBookDetailRepository.save(comicBookDetail);
	}

	public int findComicBookMaxPage(Long comicBookUid) throws Exception {
		Integer page = comicBookDetailRepository.findMaxPage(comicBookUid);
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

	public ComicBookDetail findComicBookDetailByImageUid(Long imageUid) throws Exception {
		return comicBookDetailRepository.findByImageUid(imageUid).orElseThrow(() -> new CommonException(ExceptionCode.CANNOT_FIND_MATCHED_ITEM));
	}

	public List<ComicBookEpisodeResp> getEpisodeResps(Page page) throws Exception {
		List<ComicBookEpisodeResp> comicBookEpisodeResps = new ArrayList<>();
		for(ComicBook comicBook : page.getComicBooks()) {
			comicBookEpisodeResps.add(new ComicBookEpisodeResp(comicBook, ApplicationConstants.COMIC_BOOK_UNIT_1));
		}
		comicBookEpisodeResps.sort(Comparator.comparing(ComicBookEpisodeResp::getVolume));
		return comicBookEpisodeResps;
	}

	public KitStatus getInstallStatus(Long pageUid, List<KitLog> kitLogs) throws Exception {
		return kitLogs.stream()
			.filter(v -> v.getPageUid().equals(pageUid))
			.findFirst()
			.map(v -> {
				if (v.getCreatedAt().plusDays(1).isBefore(LocalDateTime.now())) {
					return KitStatus.EXPIRED;
				} else {
					return KitStatus.AVAILABLE;
				}
			})
			.orElse(KitStatus.NEVER_USE);
	}
}
