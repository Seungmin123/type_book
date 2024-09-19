package com.muzlive.kitpage.kitpage.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ImageCode {

	COVER_IMAGE("COVER_IMAGE"),
	MUSIC_COVER_IMAGE("MUSIC_COVER_IMAGE"),
	VIDEO_COVER_IMAGE("VIDEO_COVER_IMAGE");

	private String code;
}
