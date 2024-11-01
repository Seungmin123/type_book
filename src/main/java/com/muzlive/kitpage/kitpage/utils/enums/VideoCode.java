package com.muzlive.kitpage.kitpage.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum VideoCode {

	DEFAULT("DEFAULT"),
	COMIC_BOOK("COMIC_BOOK"),
	S3("S3"),
	STREAM("STREAM"),
	YOUTUBE("YOUTUBE"),
	BITMOVIN("BITMOVIN");

	private String code;
}
