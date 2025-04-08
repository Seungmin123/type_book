package com.muzlive.kitpage.kitpage.usecase.command;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CheckTagCommand {

	private final String serialNumber;

	private final String jwt;

}
