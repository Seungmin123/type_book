package com.muzlive.kitpage.kitpage.utils.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TokenType {

	ACCESS("ACCESS"),
	CHECK_TAG("CHECK_TAG"),
	LOGIN("LOGIN");

	private final String code;

}
