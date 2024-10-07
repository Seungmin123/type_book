package com.muzlive.kitpage.kitpage.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum KitStatus {

	AVAILABLE("AVAILABLE"),
	EXPIRED("EXPIRED"),
	NEVER_USE("NEVER_USE");

	private String code;
}
