package com.muzlive.kitpage.kitpage.service.page.converter;

import com.muzlive.kitpage.kitpage.domain.page.comicbook.ComicBook;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.ComicBookDetail;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp.ComicBookImageResp;
import com.muzlive.kitpage.kitpage.domain.page.dto.resp.CommonEpisodeResp;
import com.muzlive.kitpage.kitpage.utils.enums.ClientPlatformType;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class ComicBookEpisodeConverter implements EpisodeConverter<ComicBook, CommonEpisodeResp> {

	@Override
	public CommonEpisodeResp convert(ComicBook comicBook, ClientPlatformType clientPlatformType) {
		if(CollectionUtils.isEmpty(comicBook.getComicBookDetails())) {
			return new CommonEpisodeResp(Collections.emptyList(), 0, null);
		}

		List<ComicBookImageResp> details = comicBook.getComicBookDetails().stream()
			.map(ComicBookImageResp::of)
			.collect(Collectors.toList());

		LocalDateTime lastModifiedAt = comicBook.getComicBookDetails().stream()
			.map(ComicBookDetail::getModifiedAt)
			.filter(Objects::nonNull)
			.max(LocalDateTime::compareTo)
			.orElse(null);

		return new CommonEpisodeResp(details, details.size(), lastModifiedAt);
	}
}
