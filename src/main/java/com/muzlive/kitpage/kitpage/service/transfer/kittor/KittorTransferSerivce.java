package com.muzlive.kitpage.kitpage.service.transfer.kittor;

import com.muzlive.kitpage.kitpage.config.exception.CommonException;
import com.muzlive.kitpage.kitpage.config.exception.ExceptionCode;
import com.muzlive.kitpage.kitpage.config.transfer.domain.KittorDomain;
import com.muzlive.kitpage.kitpage.domain.common.dto.resp.Result;
import com.muzlive.kitpage.kitpage.domain.common.dto.resp.SimpleResult;
import com.muzlive.kitpage.kitpage.service.transfer.kittor.dto.req.KittorChangePasswordReq;
import com.muzlive.kitpage.kitpage.service.transfer.kittor.dto.req.KittorResetPasswordReq;
import com.muzlive.kitpage.kitpage.service.transfer.kittor.dto.req.KittorUserReq;
import com.muzlive.kitpage.kitpage.service.transfer.kittor.dto.req.SendVerificationReq;
import com.muzlive.kitpage.kitpage.service.transfer.kittor.dto.resp.KittorSimpleResult;
import com.muzlive.kitpage.kitpage.service.transfer.kittor.dto.resp.KittorTokenResp;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
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

    private WebClient webClient;

    public KittorTransferSerivce(WebClient.Builder builder
            , KittorDomain kittorDomain
    ) {
        this.webClient = builder.baseUrl(kittorDomain.getDomain()).build();
    }

    public KittorTokenResp userJoin(KittorUserReq kittorUserReq) throws Exception {
        Result<KittorTokenResp> result = webClient.post()
            .uri(JOIN_URL)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(kittorUserReq), KittorUserReq.class)
            .retrieve().bodyToMono(new ParameterizedTypeReference<Result<KittorTokenResp>>() {})
            .timeout(Duration.ofMillis(15000))
            .doOnError(e -> {
                log.error(e.getMessage());
            }).block();

        if(result.getData() == null) throw new CommonException(ExceptionCode.KITTOR_EXTERNAL_SERVER_ERROR);
        return result.getData();
    }

    public KittorTokenResp userLogin(KittorUserReq kittorUserReq) throws Exception {
        Result<KittorTokenResp> result = webClient.post()
            .uri(LOGIN_URL)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(kittorUserReq), KittorUserReq.class)
            .retrieve().bodyToMono(new ParameterizedTypeReference<Result<KittorTokenResp>>() {})
            .timeout(Duration.ofMillis(15000))
            .doOnError(e -> {
                log.error(e.getMessage());
            }).block();

        if(result.getData() == null) throw new CommonException(ExceptionCode.KITTOR_EXTERNAL_SERVER_ERROR);
        return result.getData();
    }

    public Boolean sendVerificationCode(SendVerificationReq sendVerificationReq) throws Exception {
        Result<KittorSimpleResult> result = webClient.post()
            .uri(SEND_VERIFICATION_URL)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(sendVerificationReq), SendVerificationReq.class)
            .retrieve().bodyToMono(new ParameterizedTypeReference<Result<KittorSimpleResult>>() {})
            .timeout(Duration.ofMillis(15000))
            .doOnError(e -> {
                log.error(e.getMessage());
            }).block();

        if(result.getData() == null ||
            result.getData().getResult() == null)
            throw new CommonException(ExceptionCode.KITTOR_EXTERNAL_SERVER_ERROR);
        return result.getData().getResult();
    }

    public Boolean resetPassword(KittorResetPasswordReq kittorResetPasswordReq) throws Exception {
        SimpleResult result = webClient.post()
            .uri(RESET_PASSWORD_URL)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(kittorResetPasswordReq), KittorResetPasswordReq.class)
            .retrieve().bodyToMono(new ParameterizedTypeReference<SimpleResult>() {})
            .timeout(Duration.ofMillis(15000))
            .doOnError(e -> {
                log.error(e.getMessage());
            }).block();

        if(result.getStatus() != 200) throw new CommonException(ExceptionCode.KITTOR_EXTERNAL_SERVER_ERROR);
        return true;
    }

    public Boolean changePassword(String accessToken, KittorChangePasswordReq kittorChangePasswordReq) throws Exception  {
        SimpleResult result = webClient.post()
            .uri(CHANGE_PASSWORD_URL)
            .headers(headers -> {headers.setBearerAuth(accessToken);})
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(kittorChangePasswordReq), KittorChangePasswordReq.class)
            .retrieve().bodyToMono(new ParameterizedTypeReference<SimpleResult>() {})
            .timeout(Duration.ofMillis(15000))
            .doOnError(e -> {
                log.error(e.getMessage());
            }).block();

        if(result.getStatus() != 200) throw new CommonException(ExceptionCode.KITTOR_EXTERNAL_SERVER_ERROR);
        return true;
    }

}
