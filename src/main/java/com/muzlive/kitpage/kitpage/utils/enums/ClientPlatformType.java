package com.muzlive.kitpage.kitpage.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ClientPlatformType {

	AND("android"),
	IOS("ios"),
	PC("pc"),
	ANONYMOUS("anonymous");

	private String code;

	public static ClientPlatformType getClientPlatformTypeByCode(String code) {
		for(ClientPlatformType item : ClientPlatformType.values()) {
			if(item.getCode().equals(code)){
				return item;
			}
		}

		return ClientPlatformType.ANONYMOUS;
	}
}
