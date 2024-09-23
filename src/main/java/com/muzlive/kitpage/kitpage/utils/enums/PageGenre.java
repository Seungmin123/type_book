package com.muzlive.kitpage.kitpage.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PageGenre {

	ROMANCE("ROMANCE"),
	FANTASY("FANTASY"),
	ACTION("ACTION"),
	SLICE_OF_LIFE("SLICE_OF_LIFE"),
	THRILLER("THRILLER"),
	COMEDY("COMEDY"),
	DRAMA("DRAMA"),
	SCI_FI("SCI_FI"),
	SUPERHERO("SUPERHERO"),
	SUPERNATURAL("SUPERNATURAL"),
	OTHER("OTHER");

	private String code;
}