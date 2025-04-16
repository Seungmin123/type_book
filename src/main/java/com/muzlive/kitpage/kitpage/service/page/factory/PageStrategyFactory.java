package com.muzlive.kitpage.kitpage.service.page.factory;

import com.muzlive.kitpage.kitpage.domain.page.dto.resp.CommonContentDetailResp;
import com.muzlive.kitpage.kitpage.domain.page.dto.resp.CommonContentResp;
import com.muzlive.kitpage.kitpage.service.page.strategy.PageStrategy;
import com.muzlive.kitpage.kitpage.utils.enums.PageContentType;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class PageStrategyFactory {

	private final Map<PageContentType, PageStrategy<?, ?>> strategies;

	public PageStrategyFactory(List<PageStrategy<?, ?>> strategyList) {
		Map<PageContentType, PageStrategy<?, ?>> temp = new EnumMap<>(PageContentType.class);
		for (PageStrategy<?, ?> strategy : strategyList) {
			PageContentType type = strategy.getSupportedType();
			if (temp.containsKey(type)) {
				throw new IllegalStateException("Duplicate strategy found for type: " + type);
			}
			temp.put(type, strategy);
		}
		this.strategies = Collections.unmodifiableMap(temp);
	}

	@SuppressWarnings("unchecked")
	public <T extends CommonContentResp, D extends CommonContentDetailResp> PageStrategy<T, D> getStrategy(PageContentType type) {
		PageStrategy<?, ?> strategy = strategies.get(type);
		if (strategy == null) {
			throw new IllegalArgumentException("Unsupported PageContentType: " + type);
		}
		return (PageStrategy<T, D>) strategy;
	}

}
