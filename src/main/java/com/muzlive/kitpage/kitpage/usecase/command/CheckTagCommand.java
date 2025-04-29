package com.muzlive.kitpage.kitpage.usecase.command;

import com.muzlive.kitpage.kitpage.utils.enums.ClientPlatformType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CheckTagCommand {

	private final String serialNumber;

	private final String jwt;

	private final ClientPlatformType clientPlatformType;

}
