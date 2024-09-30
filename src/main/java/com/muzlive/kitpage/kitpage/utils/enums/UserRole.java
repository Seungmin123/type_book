package com.muzlive.kitpage.kitpage.utils.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {

	GUEST("ROLE_GUEST", "손님"),
	HALF_LINKER("ROLE_HALF_LINKER", "태그"),
	LINKER("ROLE_LINKER", "로그인");

	private final String key;

	private final String title;
}
