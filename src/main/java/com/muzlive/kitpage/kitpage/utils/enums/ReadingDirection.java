package com.muzlive.kitpage.kitpage.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ReadingDirection {

	LEFT("LEFT"),
	RIGHT("RIGHT"),
	DOWN("DOWN");

	private String code;
}
