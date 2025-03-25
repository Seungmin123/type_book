package com.muzlive.kitpage.kitpage.domain.common.dto.req;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageReq {

	private String searchValue;

	private int listSize = 20;

}
