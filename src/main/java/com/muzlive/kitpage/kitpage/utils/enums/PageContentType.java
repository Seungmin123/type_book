package com.muzlive.kitpage.kitpage.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PageContentType {

	COMICBOOK("COMICBOOK", "만화"),
	PHOTOBOOK("PHOTOBOOK", "화보");

	private final String code;

	private final String name;

}
