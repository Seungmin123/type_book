package com.muzlive.kitpage.kitpage.usecase;

import com.muzlive.kitpage.kitpage.config.jwt.JwtTokenProvider;
import com.muzlive.kitpage.kitpage.domain.page.Page;
import com.muzlive.kitpage.kitpage.domain.user.TokenLog;
import com.muzlive.kitpage.kitpage.domain.user.dto.resp.CheckTagResp;
import com.muzlive.kitpage.kitpage.service.page.ComicService;
import com.muzlive.kitpage.kitpage.service.page.UserService;
import com.muzlive.kitpage.kitpage.service.transfer.kihno.KihnoV2TransferSerivce;
import com.muzlive.kitpage.kitpage.service.transfer.kihno.dto.req.KihnoKitCheckReq;
import com.muzlive.kitpage.kitpage.usecase.command.CheckTagCommand;
import com.muzlive.kitpage.kitpage.utils.CommonUtils;
import com.muzlive.kitpage.kitpage.utils.constants.ApplicationConstants;
import com.muzlive.kitpage.kitpage.utils.enums.TokenType;
import com.muzlive.kitpage.kitpage.utils.enums.UserRole;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CheckTagUseCase {

	private final UserService userService;

	private final ComicService comicService;

	private final KihnoV2TransferSerivce kihnoV2TransferSerivce;

	private final JwtTokenProvider jwtTokenProvider;

	private final CommonUtils commonUtils;

	public CheckTagResp execute(CheckTagCommand command) throws Exception {
		String serialNumber = sanitizeSerial(command.getSerialNumber());
		String deviceId = jwtTokenProvider.getDeviceIdByToken(command.getJwt());
		Page page = comicService.findPageWithComicBooksBySerialNumber(serialNumber);

		KihnoKitCheckReq kihnoKitCheckReq = KihnoKitCheckReq.builder()
			.deviceId(deviceId)
			.kitId(extendSerial(command.getSerialNumber()))
			.countryCode(page == null ? ApplicationConstants.KOR_COUNTRY_CODE : page.getContent().getRegion().getCode())
			.build();

		Long kihnoKitUid = kihnoV2TransferSerivce.kihnoKitCheck(kihnoKitCheckReq).getKihnoKitUid();
		userService.selectAndUpdateKit(serialNumber, deviceId, kihnoKitUid);

		Set<String> roles = jwtTokenProvider.addRolesByToken(command.getJwt(), UserRole.HALF_LINKER);

		String token = jwtTokenProvider.createAccessToken(deviceId, serialNumber, roles);
		userService.insertTokenLog(
			TokenLog.builder()
				.token(token)
				.deviceId(deviceId)
				.serialNumber(serialNumber)
				.tokenType(TokenType.CHECK_TAG)
				.build());

		CheckTagResp checkTagResp = new CheckTagResp(page, token);

		long totalSize = comicService.getImageSizeByPageUid(page.getPageUid());
		checkTagResp.setTotalSize(totalSize);

		return checkTagResp;
	}

	private String sanitizeSerial(String serial) {
		return serial.length() > 8 ? serial.substring(0, 8) : serial;
	}

	private String extendSerial(String serial) {
		return serial.length() < 10 ? serial + commonUtils.makeRandomHexString() : serial;
	}

}
