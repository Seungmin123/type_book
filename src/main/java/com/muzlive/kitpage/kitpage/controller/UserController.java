package com.muzlive.kitpage.kitpage.controller;

import com.muzlive.kitpage.kitpage.config.aspect.ClientPlatform;
import com.muzlive.kitpage.kitpage.config.encryptor.AesSecurityProvider;
import com.muzlive.kitpage.kitpage.config.exception.CommonException;
import com.muzlive.kitpage.kitpage.config.exception.ExceptionCode;
import com.muzlive.kitpage.kitpage.config.aspect.CurrentToken;
import com.muzlive.kitpage.kitpage.config.jwt.JwtTokenProvider;
import com.muzlive.kitpage.kitpage.domain.common.dto.resp.CommonResp;
import com.muzlive.kitpage.kitpage.domain.page.Page;
import com.muzlive.kitpage.kitpage.domain.page.dto.resp.MyKitResp;
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
import com.muzlive.kitpage.kitpage.service.transfer.kittor.dto.req.KittorAccountCloseReq;
import com.muzlive.kitpage.kitpage.service.transfer.kittor.dto.req.KittorAppUserLoginReq;
import com.muzlive.kitpage.kitpage.service.transfer.kittor.dto.req.KittorChangePasswordReq;
import com.muzlive.kitpage.kitpage.service.transfer.kittor.dto.req.KittorGetPreSignedUrlReq;
import com.muzlive.kitpage.kitpage.service.transfer.kittor.dto.req.KittorOAuthLoginReq;
import com.muzlive.kitpage.kitpage.service.transfer.kittor.dto.req.KittorResetPasswordReq;
import com.muzlive.kitpage.kitpage.service.transfer.kittor.dto.req.KittorUpdateProfileReq;
import com.muzlive.kitpage.kitpage.service.transfer.kittor.dto.req.KittorUpdateProfileValidNickNameReq;
import com.muzlive.kitpage.kitpage.service.transfer.kittor.dto.req.KittorWebUserLoginReq;
import com.muzlive.kitpage.kitpage.service.transfer.kittor.dto.req.KittorWebUserJoinReq;
import com.muzlive.kitpage.kitpage.service.transfer.kittor.dto.req.SendVerificationReq;
import com.muzlive.kitpage.kitpage.service.transfer.kittor.dto.resp.KittorAppUserLoginResp;
import com.muzlive.kitpage.kitpage.service.transfer.kittor.dto.resp.KittorOAuthLoginResp;
import com.muzlive.kitpage.kitpage.service.transfer.kittor.dto.resp.KittorPreSignedUrlResp;
import com.muzlive.kitpage.kitpage.service.transfer.kittor.dto.resp.KittorProfileResp;
import com.muzlive.kitpage.kitpage.service.transfer.kittor.dto.resp.KittorTokenResp;
import com.muzlive.kitpage.kitpage.usecase.CheckTagUseCase;
import com.muzlive.kitpage.kitpage.usecase.command.CheckTagCommand;
import com.muzlive.kitpage.kitpage.utils.CommonUtils;
import com.muzlive.kitpage.kitpage.utils.enums.ClientPlatformType;
import com.muzlive.kitpage.kitpage.utils.enums.KitStatus;
import com.muzlive.kitpage.kitpage.utils.enums.TokenType;
import com.muzlive.kitpage.kitpage.utils.enums.UserRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Tag(name = "사용자 관리 API", description = "회원 관련 API 명세 https://www.notion.so/muzlive/5bd8500944f4404e8caa749559552525?pvs=4")
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
		@Parameter(
			name = "Authorization Bearer ",
			description = "JWT",
			in = ParameterIn.HEADER
		)
		@CurrentToken String jwt,
		@Parameter(
			name = "X-Client-Platform",
			description = "클라이언트 플랫폼 정보",
			in = ParameterIn.HEADER
		)
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

	@Operation(summary = "마이 페이지 - 앨범 목록 API (Kit 단위)")
	@GetMapping("/kit/list")
	public CommonResp<List<MyKitResp>> getKitList(
		@Parameter(
			name = "Authorization Bearer ",
			description = "JWT",
			in = ParameterIn.HEADER
		)
		@CurrentToken String jwt
	) throws Exception {
		return new CommonResp<>(userService.findKitByDeviceIdOrderByModifiedAtDesc(jwtTokenProvider.getDeviceIdByToken(jwt)));
	}

	@Operation(summary = "키트 삭제 API")
	@DeleteMapping("/kit/{pageUid}")
	public CommonResp<Boolean> deleteKit(
		@Parameter(
			name = "Authorization Bearer ",
			description = "JWT",
			in = ParameterIn.HEADER
		)
		@CurrentToken String jwt,
		@PathVariable Long pageUid
	) throws Exception {
		userService.deleteKit(jwtTokenProvider.getDeviceIdByToken(jwt), pageUid);
		return new CommonResp<>(true);
	}

	@Deprecated
	@Operation(summary = "키트 태그 기록 초기화 API")
	@PutMapping("/clear")
	public CommonResp<Void> clearTagHistory(
		@Parameter(
			name = "Authorization Bearer ",
			description = "JWT",
			in = ParameterIn.HEADER
		)
		@CurrentToken String jwt
	) throws Exception {
		userService.clearDeviceIdHistory(jwtTokenProvider.getDeviceIdByToken(jwt));
		return new CommonResp<>();
	}

	// --------------------------------------- Member - External Kittor Server

	@Operation(summary = "회원가입 API")
	@PostMapping("/join")
	public CommonResp<String> userJoin(
		@Valid @RequestBody KittorWebUserJoinReq kittorWebUserJoinReq,
		@Parameter(
			name = "Authorization Bearer ",
			description = "JWT",
			in = ParameterIn.HEADER
		)
		@CurrentToken String jwt
	) throws Exception {

		KittorTokenResp resp = kittorTransferSerivce.userJoin(kittorWebUserJoinReq);

		Member member = userService.findByDeviceId(jwtTokenProvider.getDeviceIdByToken(jwt));
		member.setKittorToken(resp.getAccessToken());
		userService.saveMemberAndLog(member);

		Set<String> roles = jwtTokenProvider.getRolesByToken(jwt);
		roles.add(UserRole.LINKER.getKey());
		String token = jwtTokenProvider.createAccessToken(member.getDeviceId(), kittorWebUserJoinReq.getEmail(), roles);
		userService.insertTokenLog(
			TokenLog.builder()
				.token(token)
				.deviceId(member.getDeviceId())
				.email(kittorWebUserJoinReq.getEmail())
				.tokenType(TokenType.JOIN)
				.build());

		return new CommonResp<>(token);
	}

	@Operation(summary = "자동 로그인 API",
	description = "이메일 자동 로그인 - 이메일, 인코딩된 패스워드 사용<br>"
		+ "OAuth2 자동 로그인 - provider 추가(google, apple) 그 외 이메일, 패스워드 미사용")
	@PostMapping("/login")
	public CommonResp<String> userLogin(
		@Valid @RequestBody KittorAppUserLoginReq kittorAppUserLoginReq,
		@Parameter(
			name = "Authorization Bearer ",
			description = "JWT",
			in = ParameterIn.HEADER
		)
		@CurrentToken String jwt
	) throws Exception {
		String deviceId = jwtTokenProvider.getDeviceIdByToken(jwt);
		kittorAppUserLoginReq.setDeviceId(deviceId);

		Member member = userService.findByDeviceId(jwtTokenProvider.getDeviceIdByToken(jwt));

		if(member.getDeviceId() == null)
			member.setDeviceId(deviceId);

		if(kittorAppUserLoginReq.getProvider() != null &&
			(kittorAppUserLoginReq.getProvider().equals("google") || kittorAppUserLoginReq.getProvider().equals("apple"))) {
			kittorAppUserLoginReq.setRefreshToken(member.getKittorRefreshToken());
		}

		KittorAppUserLoginResp resp = kittorTransferSerivce.appUserLogin(kittorAppUserLoginReq);
		member.setKittorToken(resp.getAccessToken());
		member.setKittorRefreshToken(resp.getRefreshToken());
		userService.saveMemberAndLog(member);

		Set<String> roles = jwtTokenProvider.getRolesByToken(jwt);
		roles.add(UserRole.LINKER.getKey());
		String token = jwtTokenProvider.createAccessToken(member.getDeviceId(), kittorAppUserLoginReq.getEmail(), roles);
		userService.insertTokenLog(
			TokenLog.builder()
				.token(token)
				.deviceId(member.getDeviceId())
				.email(kittorAppUserLoginReq.getEmail())
				.tokenType(TokenType.LOGIN)
				.build());

		return new CommonResp<>(token);
	}

	@Operation(summary = "일반 로그인 API")
	@PostMapping("/login/text")
	public CommonResp<String> userTextLogin(
		@Valid @RequestBody KittorAppUserLoginReq kittorAppUserLoginReq,
		@Parameter(
			name = "Authorization Bearer ",
			description = "JWT",
			in = ParameterIn.HEADER
		)
		@CurrentToken String jwt
	) throws Exception {
		String deviceId = jwtTokenProvider.getDeviceIdByToken(jwt);
		kittorAppUserLoginReq.setDeviceId(deviceId);

		Member member = userService.findByDeviceId(jwtTokenProvider.getDeviceIdByToken(jwt));

		if(member.getDeviceId() == null)
			member.setDeviceId(deviceId);

		KittorAppUserLoginResp resp = kittorTransferSerivce.appUserTextLogin(kittorAppUserLoginReq);
		member.setKittorToken(resp.getAccessToken());
		member.setKittorRefreshToken(resp.getRefreshToken());
		userService.saveMemberAndLog(member);

		Set<String> roles = jwtTokenProvider.getRolesByToken(jwt);
		roles.add(UserRole.LINKER.getKey());
		String token = jwtTokenProvider.createAccessToken(member.getDeviceId(), kittorAppUserLoginReq.getEmail(), roles);
		userService.insertTokenLog(
			TokenLog.builder()
				.token(token)
				.deviceId(member.getDeviceId())
				.email(kittorAppUserLoginReq.getEmail())
				.tokenType(TokenType.LOGIN)
				.build());

		return new CommonResp<>(token);
	}

	@Operation(summary = "인증코드 발송 API", description = "비밀번호 변경용 인증코드 발송")
	@PostMapping("/send/verification-code")
	public CommonResp<Boolean> sendVerificationCode(
		@Valid @RequestBody SendVerificationReq sendVerificationReq
	) throws Exception {
		return new CommonResp<>(kittorTransferSerivce.sendVerificationCode(sendVerificationReq));
	}

	@Operation(summary = "비밀번호 초기화 API", description = "인증코드 발송 API 를 통한 비밀번호 초기화")
	@PostMapping("/password/reset")
	public CommonResp<Boolean> resetPassword(
		@Valid @RequestBody KittorResetPasswordReq kittorResetPasswordReq
	) throws Exception {
		return new CommonResp<>(kittorTransferSerivce.resetPassword(kittorResetPasswordReq));
	}

	@Operation(summary = "비밀번호 변경 API")
	@PostMapping("/password/change")
	public CommonResp<Boolean> changePassword(
		@Valid @RequestBody KittorChangePasswordReq kittorChangePasswordReq,
		@Parameter(
			name = "Authorization Bearer ",
			description = "JWT",
			in = ParameterIn.HEADER
		)
		@CurrentToken String jwt
	) throws Exception {
		Member member = userService.findByDeviceId(jwtTokenProvider.getDeviceIdByToken(jwt));
		return new CommonResp<>(kittorTransferSerivce.changePassword(member.getKittorToken(), kittorChangePasswordReq));
	}

	@Operation(summary = "OAuth 로그인 콜백 API",
		description = "OAuth 로그인 후 토큰을 이용한 콜백 호출용 API<br>"
			+ "구글 로그인 - google<br>"
			+ "애플 로그인 - apple")
	@PostMapping("/oauth/callback/{provider}")
	public CommonResp<String> oAuthCallback(
		@PathVariable String provider,
		@Valid @RequestBody KittorOAuthLoginReq kittorChangePasswordReq,
		@Parameter(
			name = "Authorization Bearer ",
			description = "JWT",
			in = ParameterIn.HEADER
		)
		@CurrentToken String jwt
	) throws Exception {
		String deviceId = jwtTokenProvider.getDeviceIdByToken(jwt);
		kittorChangePasswordReq.setDevice(deviceId);
		KittorOAuthLoginResp kittorOAuthLoginResp;

		switch (provider) {
			case "google":
				kittorTransferSerivce.oAuthGoogleLogin(kittorChangePasswordReq);
				break;
			case "apple":
				kittorTransferSerivce.oAuthAppleLogin(kittorChangePasswordReq);
				break;
			default:
				throw new CommonException(ExceptionCode.INVALID_REQUEST_PRAMETER);
		}

		Set<String> roles = jwtTokenProvider.getRolesByToken(jwt);
		roles.add(UserRole.LINKER.getKey());
		String token = jwtTokenProvider.createAccessToken(deviceId, kittorChangePasswordReq.getIdToken(), roles);
		userService.insertTokenLog(
			TokenLog.builder()
				.token(token)
				.deviceId(deviceId)
				.email(kittorChangePasswordReq.getIdToken())
				.tokenType(TokenType.LOGIN)
				.build());

		return new CommonResp<>(token);
	}

	@Operation(summary = "프로필 조회 API")
	@GetMapping("/profile")
	public CommonResp<KittorProfileResp> changePassword(
		@Parameter(
			name = "Authorization Bearer ",
			description = "JWT",
			in = ParameterIn.HEADER
		)
		@CurrentToken String jwt
	) throws Exception {
		Member member = userService.findByDeviceId(jwtTokenProvider.getDeviceIdByToken(jwt));
		return new CommonResp<>(kittorTransferSerivce.getProfile(member.getKittorToken()));
	}

	@Operation(summary = "프로필 닉네임 사용 가능 여부 확인 API")
	@PostMapping("/profile/verify")
	public CommonResp<Boolean> verifyNickName(
		@RequestBody @Valid KittorUpdateProfileValidNickNameReq kittorUpdateProfileValidNickNameReq,
		@Parameter(
			name = "Authorization Bearer ",
			description = "JWT",
			in = ParameterIn.HEADER
		)
		@CurrentToken String jwt
	) throws Exception {
		Member member = userService.findByDeviceId(jwtTokenProvider.getDeviceIdByToken(jwt));
		return new CommonResp<>(kittorTransferSerivce.verifyNickName(member.getKittorToken(), kittorUpdateProfileValidNickNameReq));
	}

	@Operation(summary = "프로필 수정 API",
		description = "nickname : 변경하고자하는 닉네임 <br>"
			+ "profileFileUrl :  \"uploaded/path/\", // 실제 파일 업로드 과정 완료 후의 해당 경로 <br>"
			+ "profileFileName : \"file.png\" // 실제 파일 업로드 과정 완료 후의 해당 파일명.확장자")
	@PostMapping("/profile/update")
	public CommonResp<Boolean> verifyNickName(
		@RequestBody @Valid KittorUpdateProfileReq kittorUpdateProfileReq,
		@Parameter(
			name = "Authorization Bearer ",
			description = "JWT",
			in = ParameterIn.HEADER
		)
		@CurrentToken String jwt
	) throws Exception {
		Member member = userService.findByDeviceId(jwtTokenProvider.getDeviceIdByToken(jwt));
		return new CommonResp<>(kittorTransferSerivce.updateProfile(member.getKittorToken(), kittorUpdateProfileReq));
	}

	@Operation(summary = "프로필 수정 - 프로필 이미지 pre-signed url 조회 API",
		description = "Request <br>"
			+ "files : 리스트로 여러 개 가능(프로필의 경우 통상 1개만 사용될 것) <br>"
			+ "extension : png // 실제 파일의 확장자 <br>"
			+ "Response <br>"
			+ "fileUrl : 파일의 경로 // 이 값을 각각 수정 API의 profileFileUrl <br>"
			+ "fileName : 파일명과 확장자 // 그리고 profileName에 그대로 담아주시면 됩니다.<br>"
			+ "uploadUrl : 사용자 측에서 업로드 할 때 사용할 S3 Pre-Signed URL <br>"
			+ "resultUrl : 클라우드 프론트 조회용 URL // UI-UX 에 따라 사용되지 않을 수 있고 실제 업로드 완료 시 접근 가능")
	@PostMapping("/profile/upload/pre-signed")
	public CommonResp<KittorPreSignedUrlResp> getProfileImagePreSignedUrl(
		@RequestBody @Valid KittorGetPreSignedUrlReq kittorUpdateProfileReq,
		@Parameter(
			name = "Authorization Bearer ",
			description = "JWT",
			in = ParameterIn.HEADER
		)
		@CurrentToken String jwt
	) throws Exception {
		Member member = userService.findByDeviceId(jwtTokenProvider.getDeviceIdByToken(jwt));
		return new CommonResp<>(kittorTransferSerivce.getProfileImagePreSignedUrl(member.getKittorToken(), kittorUpdateProfileReq));
	}

	@Operation(summary = "회원 탈퇴 API",
		description = "reasons: 0 부터 선택 사유 Index <br>"
			+ "comment : 최대 500 자")
	@PostMapping("/account/close")
	public CommonResp<Boolean> accountClose(
		@RequestBody @Valid KittorAccountCloseReq kittorAccountCloseReq,
		@Parameter(
			name = "Authorization Bearer ",
			description = "JWT",
			in = ParameterIn.HEADER
		)
		@CurrentToken String jwt
	) throws Exception {
		Member member = userService.findByDeviceId(jwtTokenProvider.getDeviceIdByToken(jwt));
		return new CommonResp<>(kittorTransferSerivce.accountClose(member.getKittorToken(), kittorAccountCloseReq));
	}

}
