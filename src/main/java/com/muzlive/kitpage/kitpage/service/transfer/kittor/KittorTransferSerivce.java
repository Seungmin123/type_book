package com.muzlive.kitpage.kitpage.service.transfer.kittor;

import com.muzlive.kitpage.kitpage.config.exception.CommonException;
import com.muzlive.kitpage.kitpage.config.exception.ExceptionCode;
import com.muzlive.kitpage.kitpage.config.transfer.domain.KittorDomain;
import com.muzlive.kitpage.kitpage.domain.common.dto.resp.Result;
import com.muzlive.kitpage.kitpage.domain.common.dto.resp.SimpleResult;
import com.muzlive.kitpage.kitpage.service.transfer.kittor.dto.req.KittorChangePasswordReq;
import com.muzlive.kitpage.kitpage.service.transfer.kittor.dto.req.KittorOAuthLoginReq;
import com.muzlive.kitpage.kitpage.service.transfer.kittor.dto.req.KittorResetPasswordReq;
import com.muzlive.kitpage.kitpage.service.transfer.kittor.dto.req.KittorUserReq;
import com.muzlive.kitpage.kitpage.service.transfer.kittor.dto.req.SendVerificationReq;
import com.muzlive.kitpage.kitpage.service.transfer.kittor.dto.resp.KittorOAuthLoginResp;
import com.muzlive.kitpage.kitpage.service.transfer.kittor.dto.resp.KittorSimpleResult;
import com.muzlive.kitpage.kitpage.service.transfer.kittor.dto.resp.KittorTokenResp;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class KittorTransferSerivce {

    private final String JOIN_URL = "/v1/web/user/join";

    private final String LOGIN_URL = "/v1/web/user/login";

    private final String SEND_VERIFICATION_URL = "/v1/web/send/verification-code";

    // 인증코드 이용한 패스워드 초기화
    private final String RESET_PASSWORD_URL = "/v1/web/reset/password";

    private final String CHANGE_PASSWORD_URL = "/v1/web/user/change/password";

    // OAuth Callback
    private final String OAUTH_CALLBACK_GOOGLE_URL = "/oauth/callback/google";

    private final String OAUTH_CALLBACK_APPLE_URL = "/oauth/callback/apple";

    private WebClient webClient;

    public KittorTransferSerivce(WebClient.Builder builder
            , KittorDomain kittorDomain
    ) {
        this.webClient = builder.baseUrl(kittorDomain.getDomain()).build();
    }

    public KittorTokenResp userJoin(KittorUserReq kittorUserReq) throws Exception {
        return webClient.post()
            .uri(JOIN_URL)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(kittorUserReq), KittorUserReq.class)
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

    public KittorTokenResp userLogin(KittorUserReq kittorUserReq) throws Exception {
        return webClient.post()
            .uri(LOGIN_URL)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(kittorUserReq), KittorUserReq.class)
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

    private String getErrorMessage(SimpleResult result) {
        return result != null && result.getMessage() != null
            ? result.getMessage()
            : "Unknown error from external kittor server";
    }
}
