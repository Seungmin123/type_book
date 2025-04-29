package com.muzlive.kitpage.kitpage.controller;

import com.muzlive.kitpage.kitpage.config.aspect.ClientPlatform;
import com.muzlive.kitpage.kitpage.config.encryptor.AesSecurityProvider;
import com.muzlive.kitpage.kitpage.config.exception.CommonException;
import com.muzlive.kitpage.kitpage.config.exception.ExceptionCode;
import com.muzlive.kitpage.kitpage.config.aspect.CurrentToken;
import com.muzlive.kitpage.kitpage.config.jwt.JwtTokenProvider;
import com.muzlive.kitpage.kitpage.domain.common.dto.resp.CommonResp;
import com.muzlive.kitpage.kitpage.domain.page.Page;
import com.muzlive.kitpage.kitpage.domain.user.Kit;
import com.muzlive.kitpage.kitpage.domain.user.Member;
import com.muzlive.kitpage.kitpage.domain.user.TokenLog;
import com.muzlive.kitpage.kitpage.domain.user.dto.req.AccessTokenReq;
import com.muzlive.kitpage.kitpage.domain.user.dto.req.CheckTagReq;
import com.muzlive.kitpage.kitpage.domain.user.dto.req.KitStatusReq;
import com.muzlive.kitpage.kitpage.domain.user.dto.req.MicLocationReq;
import com.muzlive.kitpage.kitpage.domain.user.dto.req.VersionInfoReq;
import com.muzlive.kitpage.kitpage.domain.user.dto.resp.CheckTagResp;
import com.muzlive.kitpage.kitpage.domain.user.dto.resp.KitStatusResp;
import com.muzlive.kitpage.kitpage.domain.user.dto.resp.VersionInfoResp;
import com.muzlive.kitpage.kitpage.service.page.ComicService;
import com.muzlive.kitpage.kitpage.service.page.PageService;
import com.muzlive.kitpage.kitpage.service.page.UserService;
import com.muzlive.kitpage.kitpage.service.transfer.kihno.KihnoV2TransferSerivce;
import com.muzlive.kitpage.kitpage.service.transfer.kihno.dto.req.KihnoMicLocationReq;
import com.muzlive.kitpage.kitpage.service.transfer.kihno.dto.req.KihnoMicProcessedReq;
import com.muzlive.kitpage.kitpage.service.transfer.kihno.dto.resp.KihnoMicLocationResp;
import com.muzlive.kitpage.kitpage.service.transfer.kihno.dto.resp.KihnoMicProcessedResp;
import com.muzlive.kitpage.kitpage.service.transfer.kittor.KittorTransferSerivce;
import com.muzlive.kitpage.kitpage.service.transfer.kittor.dto.req.KittorChangePasswordReq;
import com.muzlive.kitpage.kitpage.service.transfer.kittor.dto.req.KittorOAuthLoginReq;
import com.muzlive.kitpage.kitpage.service.transfer.kittor.dto.req.KittorResetPasswordReq;
import com.muzlive.kitpage.kitpage.service.transfer.kittor.dto.req.KittorUserReq;
import com.muzlive.kitpage.kitpage.service.transfer.kittor.dto.req.SendVerificationReq;
import com.muzlive.kitpage.kitpage.service.transfer.kittor.dto.resp.KittorOAuthLoginResp;
import com.muzlive.kitpage.kitpage.service.transfer.kittor.dto.resp.KittorTokenResp;
import com.muzlive.kitpage.kitpage.usecase.CheckTagUseCase;
import com.muzlive.kitpage.kitpage.usecase.command.CheckTagCommand;
import com.muzlive.kitpage.kitpage.utils.CommonUtils;
import com.muzlive.kitpage.kitpage.utils.enums.ClientPlatformType;
import com.muzlive.kitpage.kitpage.utils.enums.KitStatus;
import com.muzlive.kitpage.kitpage.utils.enums.TokenType;
import com.muzlive.kitpage.kitpage.utils.enums.UserRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

	private final ComicService comicService;

	private final UserService userService;

	private final CommonUtils commonUtils;

	private final AesSecurityProvider aesSecurityProvider;

	private final CheckTagUseCase checkTagUseCase;

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
		userService.upsertMemberLog(accessTokenReq.getDeviceId(), accessTokenReq.getModelName(), commonUtils.getIp(httpServletRequest));

		return new CommonResp<>(token);
	}

	@Operation(summary = "체크 태그 API", description = "키노 서버를 통한 체크 태그 API")
	@PostMapping("/checkTag")
	public CommonResp<CheckTagResp> checkTag(
		@Valid @RequestBody CheckTagReq checkTagReq,
		@CurrentToken String jwt,
		@ClientPlatform ClientPlatformType clientPlatformType
	) throws Exception {
		return new CommonResp<>(
			checkTagUseCase.execute(
				CheckTagCommand.builder()
				.serialNumber(checkTagReq.getSerialNumber())
				.jwt(jwt)
				.clientPlatformType(clientPlatformType)
				.build())
		);
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

	@Operation(summary = "키트 점검 API")
	@GetMapping("/checkStatus")
	public CommonResp<KitStatusResp> checkKitStatus(@ModelAttribute @Valid KitStatusReq kitStatusReq) throws Exception {
		KitStatusResp kitStatusResp = new KitStatusResp();
		String serialNumber = kitStatusReq.getSerialNumber().length() > 8 ? kitStatusReq.getSerialNumber().substring(0, 8) : kitStatusReq.getSerialNumber();

		// CS ID
		kitStatusResp.setCsId(aesSecurityProvider.encrypt(serialNumber));

		Page page = userService.getPageBySerialNumber(serialNumber);
		kitStatusResp.setTitle(page.getTitle());
		kitStatusResp.setSubTitle(page.getSubTitle());
		kitStatusResp.setCoverImageUid(page.getCoverImageUid());
		kitStatusResp.setCreatedAt(page.getCreatedAt());

		kitStatusResp.setIsInstalled(
			comicService.getInstallStatus(page.getPageUid(), kitStatusReq.getDeviceId())
				.equals(KitStatus.AVAILABLE));

		return new CommonResp<>(kitStatusResp);
	}

	@Deprecated
	@Operation(summary = "인스톨 완료 API")
	@PostMapping("/install/complete")
	public CommonResp<Void> installComplete(HttpServletRequest httpServletRequest) throws Exception {
		String jwt = jwtTokenProvider.resolveToken(httpServletRequest);
		Kit kit = userService.findBySerialNumber(jwtTokenProvider.getSerialNumberByToken(jwt));
		kit.setDeviceId(jwtTokenProvider.getDeviceIdByToken(jwt));
		kit.setModifiedAt(LocalDateTime.now());
		userService.upsertKitAndLog(kit);
		return new CommonResp<>();
	}

	@Operation(summary = "키트 태그 기록 초기화 API")
	@PutMapping("/clear")
	public CommonResp<Void> clearTagHistory(@CurrentToken String jwt) throws Exception {
		userService.clearDeviceIdHistory(jwtTokenProvider.getDeviceIdByToken(jwt));
		return new CommonResp<>();
	}

	@Operation(summary = "회원가입 API")
	@PostMapping("/join")
	public CommonResp<String> userJoin(
		@Valid @RequestBody KittorUserReq kittorUserReq, @CurrentToken String jwt) throws Exception {

		KittorTokenResp resp = kittorTransferSerivce.userJoin(kittorUserReq);

		Member member = userService.findByDeviceId(jwtTokenProvider.getDeviceIdByToken(jwt));
		member.setKittorToken(resp.getAccessToken());
		userService.saveMemberAndLog(member);

		Set<String> roles = jwtTokenProvider.getRolesByToken(jwt);
		roles.add(UserRole.LINKER.getKey());
		String token = jwtTokenProvider.createAccessToken(member.getDeviceId(), kittorUserReq.getEmail(), roles);
		userService.insertTokenLog(
			TokenLog.builder()
				.token(token)
				.deviceId(member.getDeviceId())
				.email(kittorUserReq.getEmail())
				.tokenType(TokenType.JOIN)
				.build());

		return new CommonResp<>(token);
	}

	@Operation(summary = "로그인 API")
	@PostMapping("/login")
	public CommonResp<String> userLogin(
		@Valid @RequestBody KittorUserReq kittorUserReq, @CurrentToken String jwt) throws Exception {

		KittorTokenResp resp = kittorTransferSerivce.userLogin(kittorUserReq);

		Member member = userService.findByDeviceId(jwtTokenProvider.getDeviceIdByToken(jwt));
		member.setKittorToken(resp.getAccessToken());
		userService.saveMemberAndLog(member);

		Set<String> roles = jwtTokenProvider.getRolesByToken(jwt);
		roles.add(UserRole.LINKER.getKey());
		String token = jwtTokenProvider.createAccessToken(member.getDeviceId(), kittorUserReq.getEmail(), roles);
		userService.insertTokenLog(
			TokenLog.builder()
				.token(token)
				.deviceId(member.getDeviceId())
				.email(kittorUserReq.getEmail())
				.tokenType(TokenType.LOGIN)
				.build());

		return new CommonResp<>(token);
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
		@Valid @RequestBody KittorChangePasswordReq kittorChangePasswordReq, @CurrentToken String jwt) throws Exception {
		Member member = userService.findByDeviceId(jwtTokenProvider.getDeviceIdByToken(jwt));
		return new CommonResp<>(kittorTransferSerivce.changePassword(member.getKittorToken(), kittorChangePasswordReq));
	}

	@Operation(summary = "OAuth 로그인 콜백 API", description = "OAuth 로그인 후 토큰을 이용한 콜백 호출용 API")
	@PostMapping("/oauth/callback/{provider}")
	public CommonResp<KittorOAuthLoginResp> oAuthCallback(
		@PathVariable String provider,
		@Valid @RequestBody KittorOAuthLoginReq kittorChangePasswordReq,
		@CurrentToken String jwt) throws Exception {

		kittorChangePasswordReq.setDevice(jwtTokenProvider.getDeviceIdByToken(jwt));
		KittorOAuthLoginResp kittorOAuthLoginResp;

		switch (provider) {
			case "google":
				kittorOAuthLoginResp = kittorTransferSerivce.oAuthGoogleLogin(kittorChangePasswordReq);
				break;
			case "apple":
				kittorOAuthLoginResp = kittorTransferSerivce.oAuthAppleLogin(kittorChangePasswordReq);
				break;
			default:
				throw new CommonException(ExceptionCode.INVALID_REQUEST_PRAMETER);
		}

		return new CommonResp<>(kittorOAuthLoginResp);
	}

}
