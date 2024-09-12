package com.muzlive.kitpage.kitpage.utils.enums;

import lombok.Getter;

@Getter
public enum PageContentType {

	COMICBOOK("COMICBOOK");

	private final String code;

	PageContentType(String code) {
		this.code = code;
	}
}
