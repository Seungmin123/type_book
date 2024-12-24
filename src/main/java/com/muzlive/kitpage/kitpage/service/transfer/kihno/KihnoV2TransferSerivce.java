package com.muzlive.kitpage.kitpage.service.transfer.kihno;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.muzlive.kitpage.kitpage.config.exception.CommonException;
import com.muzlive.kitpage.kitpage.config.exception.ExceptionCode;
import com.muzlive.kitpage.kitpage.config.transfer.domain.KihnoV2Domain;
import com.muzlive.kitpage.kitpage.service.transfer.kihno.dto.req.KihnoKitCheckReq;
import com.muzlive.kitpage.kitpage.service.transfer.kihno.dto.req.KihnoMicLocationReq;
import com.muzlive.kitpage.kitpage.service.transfer.kihno.dto.req.KihnoMicProcessedReq;
import com.muzlive.kitpage.kitpage.service.transfer.kihno.dto.resp.KihnoKitCheckResp;
import com.muzlive.kitpage.kitpage.service.transfer.kihno.dto.resp.KihnoMicLocationResp;
import com.muzlive.kitpage.kitpage.service.transfer.kihno.dto.resp.KihnoMicProcessedResp;
import com.muzlive.kitpage.kitpage.service.transfer.kihno.dto.resp.KitPackageInfo;
import com.muzlive.kitpage.kitpage.utils.constants.ApplicationConstants;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class KihnoV2TransferSerivce {

    private final String KIT_CHECK_URL = "/PageKitCheck";

    private final String MIC_PROCESSED_URL = "/checkMicProcessed";

    private final String SELECT_MIC_URL = "/DeviceMikeEarphonePosition";

    private WebClient webClient;

    public KihnoV2TransferSerivce(WebClient.Builder builder
            , KihnoV2Domain kihnoV2Domain
    ) {
        this.webClient = builder.baseUrl(kihnoV2Domain.getDomain()).build();
    }


    // Kit Check
    public KihnoKitCheckResp kihnoKitCheck(KihnoKitCheckReq kihnoKitCheckReq) throws Exception {
        Map<String, Object> kihnoCheckTagResp = webClient.post()
                .uri(KIT_CHECK_URL)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(kihnoKitCheckReq), KihnoKitCheckReq.class)
                .retrieve().bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .timeout(Duration.ofMillis(15000))
                .doOnError(e -> log.error(e.getMessage()))
                .onErrorResume(e -> Mono.just(new HashMap<String, Object>()))
                .block();

        return this.validateKihnoData(kihnoCheckTagResp);
    }

    private KihnoKitCheckResp validateKihnoData(Map<String, Object> kihnoCheckTagResp) throws Exception {
        if(ObjectUtils.isEmpty(kihnoCheckTagResp.get(ApplicationConstants.DATA))) {
            throw new CommonException(ExceptionCode.CANNOT_FIND_MATCHED_KIHNO_ITEM);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        KihnoKitCheckResp resp = objectMapper.convertValue(kihnoCheckTagResp.get(ApplicationConstants.DATA), KihnoKitCheckResp.class);

        if(!ObjectUtils.isEmpty(resp.getErrorMsg()) && !resp.getErrorMsg().equals(ApplicationConstants.OK)) {
            throw new CommonException(HttpStatus.INTERNAL_SERVER_ERROR, resp.getErrorMsg());
        }

        if(!ObjectUtils.isEmpty(resp.getServiceType()) && !resp.getServiceType().equals(ApplicationConstants.PAGE)) {
            throw new CommonException(
                    ExceptionCode.DIFFERENT_APP_KIT
                    , new KitPackageInfo(resp.getServiceType(), resp.getServiceName(), resp.getPackageNameAnd(), resp.getPackageNameIos(), resp.getMarketId()));
        }

        return resp;
    }

    public KihnoMicLocationResp getMicLocation(KihnoMicLocationReq kihnoMicLocationReq) throws Exception {
        Map<String, Object> kihnoMicLocationResp = webClient.post()
                .uri(SELECT_MIC_URL)
                .body(Mono.just(kihnoMicLocationReq), KihnoMicLocationReq.class)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve().bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .timeout(Duration.ofMillis(15000))
                .doOnError(e -> {
                    log.info(e.getMessage());
                }).block();

        if(kihnoMicLocationResp.get(ApplicationConstants.DATA) == null) {
            throw new CommonException(ExceptionCode.CANNOT_FIND_MATCHED_KIHNO_ITEM);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        KihnoMicLocationResp resp = objectMapper.convertValue(kihnoMicLocationResp.get(ApplicationConstants.DATA), KihnoMicLocationResp.class);

        if(!ObjectUtils.isEmpty(resp.getErrorMsg()) && !resp.getErrorMsg().equals(ApplicationConstants.OK)) {
            throw new CommonException(HttpStatus.INTERNAL_SERVER_ERROR, resp.getErrorMsg());
        }

        return resp;
    }

    public KihnoMicProcessedResp checkMicProcessed(KihnoMicProcessedReq kihnoMicProcessedReq) throws Exception {
        Map<String, Object> kihnoCheckMicProcessedResp = webClient.post()
                .uri(MIC_PROCESSED_URL)
                .body(Mono.just(kihnoMicProcessedReq), KihnoMicProcessedReq.class)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve().bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .timeout(Duration.ofMillis(15000))
                .doOnError(e -> {
                    log.info(e.getMessage());
                }).block();

        if(kihnoCheckMicProcessedResp.get(ApplicationConstants.DATA) == null) {
            throw new CommonException(ExceptionCode.CANNOT_FIND_MATCHED_KIHNO_ITEM);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        KihnoMicProcessedResp resp = objectMapper.convertValue(kihnoCheckMicProcessedResp.get(ApplicationConstants.DATA), KihnoMicProcessedResp.class);
        if(!ObjectUtils.isEmpty(resp.getErrorMsg()) && !resp.getErrorMsg().equals(ApplicationConstants.OK)) {
            throw new CommonException(HttpStatus.INTERNAL_SERVER_ERROR, resp.getErrorMsg());
        }

        return resp;
    }
}
