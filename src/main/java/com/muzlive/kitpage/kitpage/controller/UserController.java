package com.muzlive.kitpage.kitpage.controller;

import com.muzlive.kitpage.kitpage.config.jwt.JwtTokenProvider;
import com.muzlive.kitpage.kitpage.domain.common.dto.resp.CommonResp;
import com.muzlive.kitpage.kitpage.domain.user.Member;
import com.muzlive.kitpage.kitpage.domain.user.TokenLog;
import com.muzlive.kitpage.kitpage.domain.user.dto.req.AccessTokenReq;
import com.muzlive.kitpage.kitpage.domain.user.dto.req.CheckTagReq;
import com.muzlive.kitpage.kitpage.domain.user.dto.req.MicLocationReq;
import com.muzlive.kitpage.kitpage.domain.user.dto.req.VersionInfoReq;
import com.muzlive.kitpage.kitpage.domain.user.dto.resp.VersionInfoResp;
import com.muzlive.kitpage.kitpage.service.page.KitService;
import com.muzlive.kitpage.kitpage.service.page.PageService;
import com.muzlive.kitpage.kitpage.service.page.UserService;
import com.muzlive.kitpage.kitpage.service.transfer.kihno.KihnoV2TransferSerivce;
import com.muzlive.kitpage.kitpage.service.transfer.kihno.dto.req.KihnoKitCheckReq;
import com.muzlive.kitpage.kitpage.service.transfer.kihno.dto.req.KihnoMicLocationReq;
import com.muzlive.kitpage.kitpage.service.transfer.kihno.dto.req.KihnoMicProcessedReq;
import com.muzlive.kitpage.kitpage.service.transfer.kihno.dto.resp.KihnoMicLocationResp;
import com.muzlive.kitpage.kitpage.service.transfer.kihno.dto.resp.KihnoMicProcessedResp;
import com.muzlive.kitpage.kitpage.service.transfer.kittor.KittorTransferSerivce;
import com.muzlive.kitpage.kitpage.service.transfer.kittor.dto.req.KittorChangePasswordReq;
import com.muzlive.kitpage.kitpage.service.transfer.kittor.dto.req.KittorResetPasswordReq;
import com.muzlive.kitpage.kitpage.service.transfer.kittor.dto.req.KittorUserReq;
import com.muzlive.kitpage.kitpage.service.transfer.kittor.dto.req.SendVerificationReq;
import com.muzlive.kitpage.kitpage.service.transfer.kittor.dto.resp.KittorTokenResp;
import com.muzlive.kitpage.kitpage.utils.CommonUtils;
import com.muzlive.kitpage.kitpage.utils.enums.TokenType;
import com.muzlive.kitpage.kitpage.utils.enums.UserRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Tag(name = "사용자 관리 API")
@RequestMapping("/v1/user")
@RestController
public class UserController {

	private final JwtTokenProvider jwtTokenProvider;

	private final KittorTransferSerivce kittorTransferSerivce;

	private final KihnoV2TransferSerivce kihnoV2TransferSerivce;

	private final PageService pageService;

	private final KitService kitService;

	private final UserService userService;

	private final CommonUtils commonUtils;

	@Operation(summary = "Token 발급 API", description = "앱 실행 시 호출, 그 이후 Header Authorization 추가")
	@PostMapping("/token")
	public CommonResp<String> createToken(@Valid @RequestBody AccessTokenReq accessTokenReq, HttpServletRequest httpServletRequest) throws Exception {

		// Token 정보 저장
		String token = jwtTokenProvider.createAccessToken(accessTokenReq.getDeviceId());

		userService.insertTokenLog(
			TokenLog.builder()
				.token(token)
				.deviceId(accessTokenReq.getDeviceId())
				.tokenType(TokenType.ACCESS)
				.build());

		// Member 정보 저장
		Member member = userService.findByDeviceId(accessTokenReq.getDeviceId());
		member.setDeviceId(accessTokenReq.getDeviceId());
		member.setModelName(accessTokenReq.getModelName());
		member.setIpAddress(commonUtils.getIp(httpServletRequest));

		userService.upsertMember(member);

		return new CommonResp<>(token);
	}

	@Operation(summary = "체크 태그 API", description = "키노 서버를 통한 체크 태그 API")
	@PostMapping("/checkTag")
	public CommonResp<String> connect(@Valid @RequestBody CheckTagReq checkTagReq, HttpServletRequest httpServletRequest) throws Exception {
		// Token DeviceID / Request DeviceID 검사
		/*String validateToken = jwtTokenProvider.resolveToken(httpServletRequest);
		if(!jwtTokenProvider.getDeviceIdByToken(validateToken).equals(checkTagReq.getDeviceId()))
			throw new CommonException(ExceptionCode.INVALID_JWT);*/

		String requestSerialNumber = (checkTagReq.getSerialNumber().length() > 8) ? checkTagReq.getSerialNumber().substring(0, 8) : checkTagReq.getSerialNumber();
		String paramSerialNumber = (checkTagReq.getSerialNumber().length() < 10) ? checkTagReq.getSerialNumber() + commonUtils.makeRandomHexString() : checkTagReq.getSerialNumber();

		KihnoKitCheckReq kihnoKitCheckReq = KihnoKitCheckReq.builder()
			.deviceId(checkTagReq.getDeviceId())
			.kitId(paramSerialNumber)
			.countryCode(checkTagReq.getRegion().getCode())
			.build();

		kitService.checkTag(checkTagReq.getDeviceId(), requestSerialNumber, kihnoV2TransferSerivce.kihnoKitCheck(kihnoKitCheckReq).getKihnoKitUid());

		// token
		String token = jwtTokenProvider.createAccessToken(checkTagReq.getDeviceId(), checkTagReq.getSerialNumber(), Set.of(UserRole.GUEST.getKey(), UserRole.HALF_LINKER.getKey()));
		userService.insertTokenLog(
			TokenLog.builder()
				.token(token)
				.deviceId(checkTagReq.getDeviceId())
				.serialNumber(checkTagReq.getSerialNumber())
				.tokenType(TokenType.CHECK_TAG)
				.build());

		// TODO 추가 정보 더하여 Response 만들기 Install 이력이 있는 컨텐츠를 전부 보여줘야하는건지 코믹북만 보여줘야하는건지?

		return new CommonResp<>(token);
	}

	@Operation(summary = "회원가입 API")
	@PostMapping("/join")
	public CommonResp<KittorTokenResp> userJoin(
		@Valid @RequestBody KittorUserReq kittorUserReq,
		HttpServletRequest request) throws Exception {

		KittorTokenResp resp = kittorTransferSerivce.userJoin(kittorUserReq);

		Member member = userService.findByDeviceId(jwtTokenProvider.getDeviceIdByToken(jwtTokenProvider.resolveToken(request)));
		member.setKittorToken(resp.getAccessToken());
		userService.upsertMember(member);

		return new CommonResp<>(resp);
	}

	@Operation(summary = "로그인 API")
	@PostMapping("/login")
	public CommonResp<KittorTokenResp> userLogin(
		@Valid @RequestBody KittorUserReq kittorUserReq,
		HttpServletRequest request) throws Exception {

		KittorTokenResp resp = kittorTransferSerivce.userLogin(kittorUserReq);

		Member member = userService.findByDeviceId(jwtTokenProvider.getDeviceIdByToken(jwtTokenProvider.resolveToken(request)));
		member.setKittorToken(resp.getAccessToken());
		userService.upsertMember(member);

		return new CommonResp<>(resp);
	}

	@Operation(summary = "인증코드 발송 API", description = "비밀번호 변경용 인증코드 발송")
	@PostMapping("/send/verification-code")
	public CommonResp<Boolean> sendVerificationCode(@Valid @RequestBody SendVerificationReq sendVerificationReq) throws Exception {
		return new CommonResp<>(kittorTransferSerivce.sendVerificationCode(sendVerificationReq));
	}

	@Operation(summary = "비밀번호 초기화 API", description = "인증코드 발송 API 를 통한 비밀번호 초기화")
	@PostMapping("/password/reset")
	public CommonResp<Boolean> resetPassword(@Valid @RequestBody KittorResetPasswordReq kittorResetPasswordReq) throws Exception {
		return new CommonResp<>(kittorTransferSerivce.resetPassword(kittorResetPasswordReq));
	}

	@Operation(summary = "비밀번호 변경 API")
	@PostMapping("/password/change")
	public CommonResp<Boolean> changePassword(
		@Valid @RequestBody KittorChangePasswordReq kittorChangePasswordReq,
		HttpServletRequest request
	) throws Exception {

		Member member = userService.findByDeviceId(jwtTokenProvider.getDeviceIdByToken(jwtTokenProvider.resolveToken(request)));
		return new CommonResp<>(kittorTransferSerivce.changePassword(member.getKittorToken(), kittorChangePasswordReq));
	}

	@Operation(summary = "마이크 Processed 체크", description = "마이크 Processed 체크")
	@GetMapping("/mic/processed")
	public CommonResp<KihnoMicProcessedResp> checkMicProcessed(@ModelAttribute @Valid MicLocationReq micLocationReq) throws Exception {
		return new CommonResp<>(kihnoV2TransferSerivce.checkMicProcessed(new KihnoMicProcessedReq(micLocationReq)));
	}

	@Operation(summary = "마이크 위치 조회 API", description = "마이크 위치 조회 API")
	@GetMapping("/mic")
	public CommonResp<KihnoMicLocationResp> getMicLocation(@ModelAttribute @Valid MicLocationReq micLocationReq) throws Exception {
		return new CommonResp<>(kihnoV2TransferSerivce.getMicLocation(new KihnoMicLocationReq(micLocationReq)));
	}

	@Operation(summary = "버전 정보 조회 API", description = "버전 정보 조회 API<br>" +
		"currentVersion:String | required | 현재 앱 버전(x.x.x)<br>" +
		"platform:String | required | AOS 또는 IOS<br>" +
		"osVersion:String | required | os 버전(x.x)")
	@GetMapping("/version")
	public CommonResp<VersionInfoResp> getVersion(@ModelAttribute @Valid VersionInfoReq versionInfoReq) throws Exception {
		return new CommonResp<>(pageService.getVersionInfo(versionInfoReq));
	}

}
