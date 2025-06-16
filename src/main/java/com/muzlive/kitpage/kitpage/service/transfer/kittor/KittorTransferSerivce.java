package com.muzlive.kitpage.kitpage.service.transfer.kittor;

import com.muzlive.kitpage.kitpage.config.exception.CommonException;
import com.muzlive.kitpage.kitpage.config.exception.ExceptionCode;
import com.muzlive.kitpage.kitpage.config.transfer.domain.KittorDomain;
import com.muzlive.kitpage.kitpage.domain.common.dto.resp.Result;
import com.muzlive.kitpage.kitpage.domain.common.dto.resp.SimpleResult;
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
import com.muzlive.kitpage.kitpage.service.transfer.kittor.dto.resp.KittorSimpleResult;
import com.muzlive.kitpage.kitpage.service.transfer.kittor.dto.resp.KittorTokenResp;
import java.time.Duration;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class KittorTransferSerivce {

    // 회원가입
    private final String JOIN_URL = "/v1/web/user/join";

    // 웹 로그인
    private final String WEB_LOGIN_URL = "/v1/web/user/login";

    // 앱 자동 로그인
    private final String APP_LOGIN_URL = "/v1/app/user/login";

    // 앱 일반 로그인
    private final String APP_TEXT_LOGIN_URL = "/v1/app/user/login/text";

    // 인증코드 발송
    private final String SEND_VERIFICATION_URL = "/v1/web/send/verification-code";

    // 인증코드 이용한 패스워드 초기화
    private final String RESET_PASSWORD_URL = "/v1/web/reset/password";

    // 비밀번호 변경
    private final String CHANGE_PASSWORD_URL = "/v1/web/user/change/password";

    // OAuth Callback - Google
    private final String OAUTH_CALLBACK_GOOGLE_URL = "/oauth/callback/google";

    // OAuth Callback - Apple
    private final String OAUTH_CALLBACK_APPLE_URL = "/oauth/callback/apple";

    // 프로필 조회 (GET)
    private final String GET_PROFILE_URL = "/v1/web/user/integration/profile";

    // 프로필 닉네임 사용 가능 여부 확인 (POST)
    private final String VERIFY_NICKNAME_URL = "/v1/web/user/integration/profile/verify";

    // 프로필 수정 (POST)
    private final String UPDATE_PROFILE_URL = "/v1/web/user/integration/profile/update";

    // 파일 업로드 준비 (POST)
    private final String GET_PROFILE_PRE_SIGNED_URL = "/v1/web/content/upload/pre-signed";

    // 회원 탈퇴 (POST)
    private final String ACCOUNT_CLOSE_URL = "/v1/web/user/account/close";

    private WebClient webClient;

    public KittorTransferSerivce(WebClient.Builder builder
            , KittorDomain kittorDomain
    ) {
        this.webClient = builder.baseUrl(kittorDomain.getDomain()).build();
    }

    public KittorTokenResp userJoin(KittorWebUserJoinReq kittorWebUserJoinReq) throws Exception {
        return webClient.post()
            .uri(JOIN_URL)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(kittorWebUserJoinReq), KittorWebUserJoinReq.class)
            .retrieve()
            .onStatus(HttpStatus::is4xxClientError, response ->
                Mono.error(new CommonException(HttpStatus.BAD_REQUEST, "Client error during join")))
            .onStatus(HttpStatus::is5xxServerError, response ->
                Mono.error(new CommonException(ExceptionCode.KITTOR_EXTERNAL_SERVER_ERROR, "Server error during join")))
            .bodyToMono(new ParameterizedTypeReference<Result<KittorTokenResp>>() {})
            .timeout(Duration.ofMillis(15000))
            .doOnError(e -> log.error("회원가입 중 오류", e))
            .flatMap(result ->
                result == null || result.getData() == null
                    ? Mono.error(new CommonException(HttpStatus.BAD_REQUEST, getErrorMessage(result)))
                    : Mono.just(result.getData())
            )
            .block();
    }

    // 미사용
    public KittorTokenResp webUserLogin(KittorWebUserLoginReq kittorWebUserLoginReq) throws Exception {
        return webClient.post()
            .uri(WEB_LOGIN_URL)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(kittorWebUserLoginReq), KittorWebUserLoginReq.class)
            .retrieve()
            .onStatus(HttpStatus::is4xxClientError, response ->
                Mono.error(new CommonException(HttpStatus.BAD_REQUEST, "Client error during login")))
            .onStatus(HttpStatus::is5xxServerError, response ->
                Mono.error(new CommonException(ExceptionCode.KITTOR_EXTERNAL_SERVER_ERROR, "Server error during login")))
            .bodyToMono(new ParameterizedTypeReference<Result<KittorTokenResp>>() {})
            .timeout(Duration.ofMillis(15000))
            .doOnError(e -> log.error("로그인 중 오류", e))
            .flatMap(result ->
                result == null || result.getData() == null
                    ? Mono.error(new CommonException(HttpStatus.BAD_REQUEST, getErrorMessage(result)))
                    : Mono.just(result.getData())
            )
            .block();
    }

    // 자동 로그인
    public KittorAppUserLoginResp appUserLogin(KittorAppUserLoginReq kittorAppUserLoginReq) throws Exception {
        return webClient.post()
            .uri(APP_LOGIN_URL)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(kittorAppUserLoginReq), KittorAppUserLoginReq.class)
            .retrieve()
            .onStatus(HttpStatus::is4xxClientError, response ->
                Mono.error(new CommonException(HttpStatus.BAD_REQUEST, "Client error during login")))
            .onStatus(HttpStatus::is5xxServerError, response ->
                Mono.error(new CommonException(ExceptionCode.KITTOR_EXTERNAL_SERVER_ERROR, "Server error during login")))
            .bodyToMono(new ParameterizedTypeReference<Result<KittorAppUserLoginResp>>() {})
            .timeout(Duration.ofMillis(15000))
            .doOnError(e -> log.error("자동 로그인 중 오류", e))
            .flatMap(result ->
                result == null || result.getData() == null
                    ? Mono.error(new CommonException(HttpStatus.BAD_REQUEST, getErrorMessage(result)))
                    : Mono.just(result.getData())
            )
            .block();
    }

    // 일반 로그인
    public KittorAppUserLoginResp appUserTextLogin(KittorAppUserLoginReq kittorAppUserLoginReq) throws Exception {
        return webClient.post()
            .uri(APP_TEXT_LOGIN_URL)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(kittorAppUserLoginReq), KittorAppUserLoginReq.class)
            .retrieve()
            .onStatus(HttpStatus::is4xxClientError, response ->
                Mono.error(new CommonException(HttpStatus.BAD_REQUEST, "Client error during text login")))
            .onStatus(HttpStatus::is5xxServerError, response ->
                Mono.error(new CommonException(ExceptionCode.KITTOR_EXTERNAL_SERVER_ERROR, "Server error during text login")))
            .bodyToMono(new ParameterizedTypeReference<Result<KittorAppUserLoginResp>>() {})
            .timeout(Duration.ofMillis(15000))
            .doOnError(e -> log.error("일반 로그인 중 오류", e))
            .flatMap(result ->
                result == null || result.getData() == null
                    ? Mono.error(new CommonException(HttpStatus.BAD_REQUEST, getErrorMessage(result)))
                    : Mono.just(result.getData())
            )
            .block();
    }

    public Boolean sendVerificationCode(SendVerificationReq sendVerificationReq) throws Exception {
        return webClient.post()
            .uri(SEND_VERIFICATION_URL)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(sendVerificationReq), SendVerificationReq.class)
            .retrieve()
            .onStatus(HttpStatus::is4xxClientError, response ->
                Mono.error(new CommonException(HttpStatus.BAD_REQUEST, "Client error during verification code")))
            .onStatus(HttpStatus::is5xxServerError, response ->
                Mono.error(new CommonException(ExceptionCode.KITTOR_EXTERNAL_SERVER_ERROR, "Server error during verification code")))
            .bodyToMono(new ParameterizedTypeReference<Result<KittorSimpleResult>>() {})
            .timeout(Duration.ofMillis(15000))
            .doOnError(e -> log.error("인증코드 점검 중 오류", e))
            .flatMap(result ->
                result == null || result.getData() == null || result.getData().getResult() == null
                    ? Mono.error(new CommonException(HttpStatus.BAD_REQUEST, getErrorMessage(result)))
                    : Mono.just(result.getData().getResult())
            )
            .block();
    }

    public Boolean resetPassword(KittorResetPasswordReq kittorResetPasswordReq) throws Exception {
        return webClient.post()
            .uri(RESET_PASSWORD_URL)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(kittorResetPasswordReq), KittorResetPasswordReq.class)
            .retrieve()
            .onStatus(HttpStatus::is4xxClientError, response ->
                Mono.error(new CommonException(HttpStatus.BAD_REQUEST, "Client error during reset password")))
            .onStatus(HttpStatus::is5xxServerError, response ->
                Mono.error(new CommonException(ExceptionCode.KITTOR_EXTERNAL_SERVER_ERROR, "Server error during reset password")))
            .bodyToMono(new ParameterizedTypeReference<SimpleResult>() {})
            .timeout(Duration.ofMillis(15000))
            .doOnError(e -> log.error("비밀번호 초기화 중 오류", e))
            .flatMap(result ->
                result == null || result.getStatus() != 200
                    ? Mono.error(new CommonException(HttpStatus.BAD_REQUEST, getErrorMessage(result)))
                    : Mono.just(true)
            )
            .block();
    }

    public Boolean changePassword(String accessToken, KittorChangePasswordReq kittorChangePasswordReq) throws Exception  {
        return webClient.post()
            .uri(CHANGE_PASSWORD_URL)
            .headers(h -> h.setBearerAuth(accessToken))
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(kittorChangePasswordReq), KittorChangePasswordReq.class)
            .retrieve()
            .onStatus(HttpStatus::is4xxClientError, response ->
                Mono.error(new CommonException(HttpStatus.BAD_REQUEST, "Client error during change password")))
            .onStatus(HttpStatus::is5xxServerError, response ->
                Mono.error(new CommonException(ExceptionCode.KITTOR_EXTERNAL_SERVER_ERROR, "Server error during change password")))
            .bodyToMono(new ParameterizedTypeReference<SimpleResult>() {})
            .timeout(Duration.ofMillis(15000))
            .doOnError(e -> log.error("비밀번호 변경 중 오류", e))
            .flatMap(result ->
                result == null || result.getStatus() != 200
                    ? Mono.error(new CommonException(HttpStatus.BAD_REQUEST, getErrorMessage(result)))
                    : Mono.just(true)
            )
            .block();
    }

    public KittorOAuthLoginResp oAuthGoogleLogin(KittorOAuthLoginReq kittorOAuthLoginReq) throws Exception {
        return webClient.post()
            .uri(OAUTH_CALLBACK_GOOGLE_URL)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(kittorOAuthLoginReq), KittorOAuthLoginReq.class)
            .retrieve()
            .onStatus(HttpStatus::is4xxClientError, response ->
                Mono.error(new CommonException(HttpStatus.BAD_REQUEST, "Client error during google login")))
            .onStatus(HttpStatus::is5xxServerError, response ->
                Mono.error(new CommonException(ExceptionCode.KITTOR_EXTERNAL_SERVER_ERROR, "Server error during google login")))
            .bodyToMono(new ParameterizedTypeReference<Result<KittorOAuthLoginResp>>() {})
            .timeout(Duration.ofMillis(15000))
            .doOnError(e -> log.error("구글 로그인 중 오류", e))
            .flatMap(result ->
                result == null || result.getData() == null
                    ? Mono.error(new CommonException(HttpStatus.BAD_REQUEST, getErrorMessage(result)))
                    : Mono.just(result.getData())
            )
            .block();
    }

    public KittorOAuthLoginResp oAuthAppleLogin(KittorOAuthLoginReq kittorOAuthLoginReq) throws Exception {
        return webClient.post()
            .uri(OAUTH_CALLBACK_APPLE_URL)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(kittorOAuthLoginReq), KittorOAuthLoginReq.class)
            .retrieve()
            .onStatus(HttpStatus::is4xxClientError, response ->
                Mono.error(new CommonException(HttpStatus.BAD_REQUEST, "Client error during apple login")))
            .onStatus(HttpStatus::is5xxServerError, response ->
                Mono.error(new CommonException(ExceptionCode.KITTOR_EXTERNAL_SERVER_ERROR, "Server error during apple login")))
            .bodyToMono(new ParameterizedTypeReference<Result<KittorOAuthLoginResp>>() {})
            .timeout(Duration.ofMillis(15000))
            .doOnError(e -> log.error("애플 로그인 중 오류", e))
            .flatMap(result ->
                result == null || result.getData() == null
                    ? Mono.error(new CommonException(HttpStatus.BAD_REQUEST, getErrorMessage(result)))
                    : Mono.just(result.getData())
            )
            .block();
    }

    public KittorProfileResp getProfile(String accessToken) throws Exception  {
        return webClient.get()
            .uri(GET_PROFILE_URL)
            .headers(h -> h.setBearerAuth(accessToken))
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .onStatus(HttpStatus::is4xxClientError, response ->
                Mono.error(new CommonException(HttpStatus.BAD_REQUEST, "Client error during get profile")))
            .onStatus(HttpStatus::is5xxServerError, response ->
                Mono.error(new CommonException(ExceptionCode.KITTOR_EXTERNAL_SERVER_ERROR, "Server error during get profile")))
            .bodyToMono(new ParameterizedTypeReference<Result<KittorProfileResp>>() {})
            .timeout(Duration.ofMillis(15000))
            .doOnError(e -> log.error("프로필 조회 중 오류", e))
            .flatMap(result ->
                result == null || result.getData() == null
                    ? Mono.error(new CommonException(HttpStatus.BAD_REQUEST, getErrorMessage(result)))
                    : Mono.just(result.getData())
            )
            .block();
    }

    public Boolean verifyNickName(String accessToken, KittorUpdateProfileValidNickNameReq kittorUpdateProfileValidNickNameReq) throws Exception  {
        return webClient.post()
            .uri(VERIFY_NICKNAME_URL)
            .headers(h -> h.setBearerAuth(accessToken))
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(kittorUpdateProfileValidNickNameReq), KittorUpdateProfileValidNickNameReq.class)
            .retrieve()
            .onStatus(HttpStatus::is4xxClientError, response ->
                Mono.error(new CommonException(HttpStatus.BAD_REQUEST, "Client error during verify nickname")))
            .onStatus(HttpStatus::is5xxServerError, response ->
                Mono.error(new CommonException(ExceptionCode.KITTOR_EXTERNAL_SERVER_ERROR, "Server error during verify nickname")))
            .bodyToMono(new ParameterizedTypeReference<SimpleResult>() {})
            .timeout(Duration.ofMillis(15000))
            .doOnError(e -> log.error("닉네임 유효성 검사 중 오류", e))
            .flatMap(result ->
                result == null || result.getStatus() != 200
                    ? Mono.error(new CommonException(HttpStatus.BAD_REQUEST, getErrorMessage(result)))
                    : Mono.just(result.getMessage().equals("Success") ? Boolean.TRUE : Boolean.FALSE)
            )
            .block();
    }

    public Boolean updateProfile(String accessToken, KittorUpdateProfileReq kittorUpdateProfileReq) throws Exception  {
        return webClient.post()
            .uri(UPDATE_PROFILE_URL)
            .headers(h -> h.setBearerAuth(accessToken))
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(kittorUpdateProfileReq), KittorUpdateProfileReq.class)
            .retrieve()
            .onStatus(HttpStatus::is4xxClientError, response ->
                Mono.error(new CommonException(HttpStatus.BAD_REQUEST, "Client error during update profile")))
            .onStatus(HttpStatus::is5xxServerError, response ->
                Mono.error(new CommonException(ExceptionCode.KITTOR_EXTERNAL_SERVER_ERROR, "Server error during update profile")))
            .bodyToMono(new ParameterizedTypeReference<SimpleResult>() {})
            .timeout(Duration.ofMillis(15000))
            .doOnError(e -> log.error("프로필 수정 중 오류", e))
            .flatMap(result ->
                result == null || result.getStatus() != 200
                    ? Mono.error(new CommonException(HttpStatus.BAD_REQUEST, getErrorMessage(result)))
                    : Mono.just(result.getMessage().equals("Success") ? Boolean.TRUE : Boolean.FALSE)
            )
            .block();
    }

    public KittorPreSignedUrlResp getProfileImagePreSignedUrl(String accessToken, KittorGetPreSignedUrlReq kittorGetPreSignedUrlReq) throws Exception  {
        return webClient.post()
            .uri(GET_PROFILE_PRE_SIGNED_URL)
            .headers(h -> h.setBearerAuth(accessToken))
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(kittorGetPreSignedUrlReq), KittorGetPreSignedUrlReq.class)
            .retrieve()
            .onStatus(HttpStatus::is4xxClientError, response ->
                Mono.error(new CommonException(HttpStatus.BAD_REQUEST, "Client error during get pre-signed url")))
            .onStatus(HttpStatus::is5xxServerError, response ->
                Mono.error(new CommonException(ExceptionCode.KITTOR_EXTERNAL_SERVER_ERROR, "Server error during get pre-signed url")))
            .bodyToMono(new ParameterizedTypeReference<Result<List<KittorPreSignedUrlResp>>>() {})
            .timeout(Duration.ofMillis(15000))
            .doOnError(e -> log.error("프로필 이미지 pre-signed Url 호출 중 오류", e))
            .flatMap(result ->
                result == null || CollectionUtils.isEmpty(result.getData())
                    ? Mono.error(new CommonException(HttpStatus.BAD_REQUEST, getErrorMessage(result)))
                    : Mono.just(result.getData().get(0))
            )
            .block();
    }

    public Boolean accountClose(String accessToken, KittorAccountCloseReq kittorAccountCloseReq) throws Exception  {
        return webClient.post()
            .uri(ACCOUNT_CLOSE_URL)
            .headers(h -> h.setBearerAuth(accessToken))
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(kittorAccountCloseReq), KittorAccountCloseReq.class)
            .retrieve()
            .onStatus(HttpStatus::is4xxClientError, response ->
                Mono.error(new CommonException(HttpStatus.BAD_REQUEST, "Client error during account close")))
            .onStatus(HttpStatus::is5xxServerError, response ->
                Mono.error(new CommonException(ExceptionCode.KITTOR_EXTERNAL_SERVER_ERROR, "Server error during account close")))
            .bodyToMono(new ParameterizedTypeReference<SimpleResult>() {})
            .timeout(Duration.ofMillis(15000))
            .doOnError(e -> log.error("회원 탈퇴 중 오류", e))
            .flatMap(result ->
                result == null || result.getStatus() != 200
                    ? Mono.error(new CommonException(HttpStatus.BAD_REQUEST, getErrorMessage(result)))
                    : Mono.just(result.getMessage().equals("Success") ? Boolean.TRUE : Boolean.FALSE)
            )
            .block();
    }

    private String getErrorMessage(SimpleResult result) {
        return result != null && result.getMessage() != null
            ? result.getMessage()
            : "Unknown error from external kittor server";
    }
}
