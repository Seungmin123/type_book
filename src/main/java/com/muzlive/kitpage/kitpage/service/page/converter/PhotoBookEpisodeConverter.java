package com.muzlive.kitpage.kitpage.service.page.converter;

import com.muzlive.kitpage.kitpage.domain.page.comicbook.ComicBookDetail;
import com.muzlive.kitpage.kitpage.domain.page.dto.resp.CommonEpisodeDetailResp;
import com.muzlive.kitpage.kitpage.domain.page.dto.resp.CommonEpisodeResp;
import com.muzlive.kitpage.kitpage.domain.page.photobook.PhotoBook;
import com.muzlive.kitpage.kitpage.domain.page.photobook.PhotoBookDetail;
import com.muzlive.kitpage.kitpage.domain.page.photobook.dto.resp.PhotoBookEpisodeDetailResp;
import com.muzlive.kitpage.kitpage.domain.page.photobook.dto.resp.PhotoBookImageResp;
import com.muzlive.kitpage.kitpage.utils.enums.ClientPlatformType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class PhotoBookEpisodeConverter implements EpisodeConverter<PhotoBook, CommonEpisodeResp> {

	@Override
	public CommonEpisodeResp convert(PhotoBook photoBook, ClientPlatformType clientPlatformType) {
		if(CollectionUtils.isEmpty(photoBook.getPhotoBookDetails())) {
			return new CommonEpisodeResp(Collections.emptyList(), 0, null);
		}

		List<CommonEpisodeDetailResp> details = photoBook.getPhotoBookDetails().stream()
			.map(detail -> {
				if(ClientPlatformType.IOS.equals(clientPlatformType)) {
					return PhotoBookEpisodeDetailResp.of(detail);
				} else {
					return PhotoBookImageResp.of(detail);
				}
			})
			.collect(Collectors.toList());

		LocalDateTime lastModifiedAt = photoBook.getPhotoBookDetails().stream()
			.map(PhotoBookDetail::getModifiedAt)
			.filter(Objects::nonNull)
			.max(LocalDateTime::compareTo)
			.orElse(null);

		return new CommonEpisodeResp(details, details.size(), lastModifiedAt);
	}
}
