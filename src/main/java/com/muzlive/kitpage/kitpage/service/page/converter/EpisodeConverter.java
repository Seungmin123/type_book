package com.muzlive.kitpage.kitpage.service.page.converter;

import com.muzlive.kitpage.kitpage.domain.page.dto.resp.CommonEpisodeResp;
import com.muzlive.kitpage.kitpage.utils.enums.ClientPlatformType;

@FunctionalInterface
public interface EpisodeConverter<E, D extends CommonEpisodeResp> {

	D convert(E episode, ClientPlatformType clientPlatformType);

}
