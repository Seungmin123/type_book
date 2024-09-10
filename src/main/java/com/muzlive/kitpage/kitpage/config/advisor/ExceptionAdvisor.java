package com.muzlive.kitpage.kitpage.config.advisor;

import com.muzlive.kitpage.kitpage.config.exception.CommonException;
import com.muzlive.kitpage.kitpage.domain.common.dto.resp.CommonResp;
import com.muzlive.kitpage.kitpage.service.transfer.webhook.WebhookTransfer;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.HandlerMapping;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class ExceptionAdvisor {

    private final WebhookTransfer webhookTransfer;

    @ExceptionHandler(value={BindException.class})
    protected CommonResp handleMethodArgumentNotValid(HttpServletRequest req, BindException ex) {
        log.error("MethodArgumentNotValidException =========================================");
        FieldError fieldError = ex.getBindingResult().getFieldErrors()
                .stream()
                .findFirst()
                .get();

        log.error(fieldError.getField() + " : " + fieldError.getDefaultMessage());

        return new CommonResp(HttpStatus.BAD_REQUEST, fieldError.getField() + " : " + fieldError.getDefaultMessage() , null);
    }

    // Zap 에러 핸들링
    @ExceptionHandler(ClientAbortException.class)
    public CommonResp handleClientAbortException(ClientAbortException e) {
        log.error("ClientAbortException =========================================");
        log.error(e.getMessage());
        return getResponseEntity(new CommonException());
    }

    // Zap 에러 핸들링
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public CommonResp handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        return new CommonResp(HttpStatus.METHOD_NOT_ALLOWED, e.getMessage(), null);
    }

    @ExceptionHandler(value = {Throwable.class})
    protected CommonResp handleThrowableExceptions(HttpServletRequest req, Exception ex){
        try {
            if (ex instanceof CommonException) {
                CommonException e = (CommonException) ex;
                return getResponseEntity(e);
             }

            log.error(ex.getMessage());

            ex.printStackTrace();
            //commonService.insertServerError(req, ex);

            //webhookTransfer.sendSlackMessage(makeSlackMessage(req, ex));

        } catch (Exception e) {
            log.error(e.getMessage());
            for (StackTraceElement element : e.getStackTrace()) {
                log.error(element.toString());
            }
            return getResponseEntity(new CommonException());
        }

        return getResponseEntity(new CommonException());
    }

    private CommonResp getResponseEntity(CommonException e) {
        log.error(e.getMessage());
        return new CommonResp(e.getStatus(), e.getMessage(), e.data);
    }

    String makeSlackMessage(HttpServletRequest req, Exception ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);

        StringBuilder slackMessage = new StringBuilder();
        slackMessage.append("\n");
        slackMessage.append("Domain: ").append(req.getServerName()).append("\n");
        slackMessage.append("URL   : ").append((String) req.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE)).append("\n");
        slackMessage.append("MSG   : ").append(StringUtils.defaultIfEmpty(ex.getMessage(), ex.toString())).append("\n");
        slackMessage.append("StackTrace   : ").append(sw.toString()).append("\n");

        return slackMessage.toString();
    }

}
