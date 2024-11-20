package com.muzlive.kitpage.kitpage.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ImageCode {

	PAGE_IMAGE("PAGE_IMAGE"),
	CONTENT_COVER_IMAGE("CONTENT_COVER_IMAGE"),
	COMIC_COVER_IMAGE("COMIC_COVER_IMAGE"),
	COMIC_IMAGE("COMIC_IMAGE"),
	MUSIC_COVER_IMAGE("MUSIC_COVER_IMAGE"),
	VIDEO_COVER_IMAGE("VIDEO_COVER_IMAGE");

	private String code;
}
