package com.muzlive.kitpage.kitpage.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Region {

	KOR("KOR"),
	ENG("ENG"),
	USA("ENG"),
	JPN("JPN");

	private String code;

	public static Region getRegionByCode(String code) {
		for(Region item : Region.values()) {
			if(item.getCode().equals(code)){
				return item;
			}
		}

		return Region.ENG;
	}
}
