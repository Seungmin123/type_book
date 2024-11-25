package com.muzlive.kitpage.kitpage.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Region {

	KOR("KOR", "KOR"),
	ENG("ENG", "ENG"),
	USA("ENG", "USA"),
	JPN("JPN", "JPN");

	private String code;

	private String name;

	public static Region getRegionByName(String name) {
		for(Region item : Region.values()) {
			if(item.getName().equals(name)){
				return item;
			}
		}

		return Region.KOR;
	}
}
