package com.muzlive.kitpage.kitpage.config.jwt;

import com.muzlive.kitpage.kitpage.config.exception.CommonException;
import com.muzlive.kitpage.kitpage.config.exception.ExceptionCode;
import com.muzlive.kitpage.kitpage.utils.constants.ApplicationConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

    @Value("${security.secretKey}")
    private String secretKey;

    private long accessTokenValidTime = (1 * 24 * 60 * 60 * 1000L);

    //---------------------------------------------------------------------------------------------

    public static final String ROLE_USER = "ROLE_USER";

    public List<String> getDefaultRoles() {
        return Collections.singletonList(ROLE_USER);
    }

    //---------------------------------------------------------------------------------------------

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    // JWT 토큰 생성
    public String createAccessToken(String deviceId, String serialNumber) {
        return createAccessToken(deviceId, serialNumber, getDefaultRoles());
    }

    public String createAccessToken(String deviceId, String serialNumber, List<String> roles) {
        Map<String, String> claims = new HashMap<>();
        return createAccessToken(deviceId, serialNumber, roles, claims);
    }

    public String createAccessToken(String deviceId, String serialNumber, List<String> roles, Map<String, String> optionalClaims) {

        Claims claims = Jwts.claims().setSubject(deviceId);
        claims.put("serialNumber", serialNumber.substring(0, 8));
        claims.put("roles", roles);
        claims.putAll(optionalClaims);

        Map<String, Object> header = new HashMap<>();
        header.put("typ", "JWT");
        header.put("alg", "HS256");

        Date now = new Date();
        return Jwts.builder()
                .setHeader(header)
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + accessTokenValidTime)) // set Expire Time
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .setHeader(header)
                .compact();
    }


    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(ApplicationConstants.AUTHORIZATION);
        final String startWith = (ApplicationConstants.BEARER + " ");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(startWith)) {
            return bearerToken.substring(startWith.length());
        }
        return null;
    }

    public boolean validateAccessToken(String accessToken) throws Exception {
        return validateAccessToken(accessToken, true);
    }
    public boolean validateAccessToken(String accessToken, boolean validateUser) throws Exception {
        try {
            final Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(accessToken);

            final String deviceId = String.valueOf(claims.getBody().getSubject());
            final String serialNumber = String.valueOf(claims.getBody().get("serialNumber"));

            return !claims.getBody().getExpiration().before(new Date());

        } catch (SecurityException | MalformedJwtException e) {
            log.error(e.getMessage());
            throw new CommonException(ExceptionCode.INVALID_JWT_SIGNATURE);
        } catch (ExpiredJwtException e) {
            log.error(e.getMessage());
            throw new CommonException(ExceptionCode.EXPIRED_JWT);
        } catch (UnsupportedJwtException e) {
            log.error(e.getMessage());
            throw new CommonException(ExceptionCode.NOT_SUPPORTED_JWT);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CommonException(ExceptionCode.INVALID_JWT);
        }
    }

    public String getDeviceIdByToken(String accessToken) throws Exception {
        final Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(accessToken);
        return String.valueOf(claims.getBody().getSubject());
    }

    public String getSerialNumberByToken(String accessToken) throws Exception {
        final Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(accessToken);
        return String.valueOf(claims.getBody().get("serialNumber"));
    }
    //---------------------------------------------------------------------------------------------

}
