package com.muzlive.kitpage.kitpage.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ImageCode {

	PAGE_IMAGE("PAGE"),
	COMIC_COVER_IMAGE("COMIC_COVER"),
	MUSIC_COVER_IMAGE("MUSIC_COVER"),
	VIDEO_COVER_IMAGE("VIDEO_COVER");

	private String code;
}
