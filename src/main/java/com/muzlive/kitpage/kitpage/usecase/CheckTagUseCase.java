package com.muzlive.kitpage.kitpage.usecase;

import com.muzlive.kitpage.kitpage.config.jwt.JwtTokenProvider;
import com.muzlive.kitpage.kitpage.domain.page.Page;
import com.muzlive.kitpage.kitpage.domain.user.TokenLog;
import com.muzlive.kitpage.kitpage.domain.user.dto.resp.CheckTagResp;
import com.muzlive.kitpage.kitpage.service.page.ComicService;
import com.muzlive.kitpage.kitpage.service.page.PageService;
import com.muzlive.kitpage.kitpage.service.page.UserService;
import com.muzlive.kitpage.kitpage.service.page.factory.PageStrategyFactory;
import com.muzlive.kitpage.kitpage.service.page.strategy.PageStrategy;
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

	private final PageService pageService;

	private final UserService userService;

	private final KihnoV2TransferSerivce kihnoV2TransferSerivce;

	private final JwtTokenProvider jwtTokenProvider;

	private final CommonUtils commonUtils;

	private final PageStrategyFactory pageStrategyFactory;

	public CheckTagResp execute(CheckTagCommand command) throws Exception {
		String serialNumber = sanitizeSerial(command.getSerialNumber());
		String deviceId = jwtTokenProvider.getDeviceIdByToken(command.getJwt());
		// TODO 못찾으면 날려버리는데 수정
		Page page = pageService.findPageBySerialNumber(serialNumber);
		PageStrategy pageStrategy = pageStrategyFactory.getStrategy(page.getContent().getContentType());

		KihnoKitCheckReq kihnoKitCheckReq = KihnoKitCheckReq.builder()
			.deviceId(deviceId)
			.kitId(extendSerial(command.getSerialNumber()))
			.countryCode(page.getContent().getRegion().getCode())
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
		checkTagResp.setTotalSize(pageStrategy.getTotalSize(page.getPageUid()));

		return checkTagResp;
	}

	private String sanitizeSerial(String serial) {
		return serial.length() > 8 ? serial.substring(0, 8) : serial;
	}

	private String extendSerial(String serial) {
		return serial.length() < 10 ? serial + commonUtils.makeRandomHexString() : serial;
	}

}
