package com.muzlive.kitpage.kitpage.config.jwt;

import com.muzlive.kitpage.kitpage.config.exception.CommonException;
import com.muzlive.kitpage.kitpage.config.exception.ExceptionCode;
import com.muzlive.kitpage.kitpage.utils.constants.ApplicationConstants;
import com.muzlive.kitpage.kitpage.utils.constants.HeaderConstants;
import com.muzlive.kitpage.kitpage.utils.enums.UserRole;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

    @Value("${security.secretKey}")
    private String secretKey;

    private long accessTokenValidTime = (1 * 24 * 60 * 60 * 1000L);

    //---------------------------------------------------------------------------------------------

    public Set<String> getDefaultRoles() {
        return Collections.singleton(UserRole.GUEST.getKey());
    }

    //---------------------------------------------------------------------------------------------

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    // JWT 토큰 생성
    public String createAccessToken(String deviceId) {
        return createAccessToken(deviceId, getDefaultRoles());
    }

    public String createAccessToken(String deviceId, Set<String> roles) {
        return createAccessToken(deviceId, null, roles);
    }

    public String createAccessToken(String deviceId, String serialNumber, Set<String> roles) {
        return createAccessToken(deviceId, serialNumber, null, roles);
    }

    /*
        Todo : 10/16 일 SerialNumber 가 null로 들어왔을 경우, substring 파싱 Exception 제외
            null 일 경우 serialnumber null로 주게 변경 -> 실제 serialnumber 사용이 없음
     */
    public String createAccessToken(String deviceId, String serialNumber, String email, Set<String> roles) {
        Map<String, String> claims = new HashMap<>();
        claims.put("serialNumber", !ObjectUtils.isEmpty(serialNumber) ? serialNumber.substring(0, 8) : null);
        claims.put("email", email);
        return createAccessToken(deviceId, serialNumber, email, roles, claims);
    }

    public String createAccessToken(String deviceId, String serialNumber, String email, Set<String> roles, Map<String, String> optionalClaims) {
        Claims claims = Jwts.claims().setSubject(deviceId);
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
                .setId(ApplicationConstants.PAGE)
                .compact();
    }


    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(HeaderConstants.AUTHORIZATION);
        final String startWith = (HeaderConstants.BEARER + " ");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(startWith)) {
            return bearerToken.substring(startWith.length());
        }
        return null;
    }

    public boolean validateAccessToken(String accessToken) {
        return validateAccessToken(accessToken, true);
    }
    public boolean validateAccessToken(String accessToken, boolean validateUser) {
        try {
            final Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(accessToken);

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

    public String getDeviceIdByToken(String accessToken) {
        final Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(accessToken);
        return String.valueOf(claims.getBody().getSubject());
    }

    public String getSerialNumberByToken(String accessToken) {
        final Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(accessToken);
        return String.valueOf(claims.getBody().get("serialNumber"));
    }

    public String getEmailByToken(String accessToken) {
        final Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(accessToken);
        return String.valueOf(claims.getBody().get("email"));
    }

    public Set<String> getRolesByToken(String accessToken) {
        final Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(accessToken);
        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) claims.getBody().get("roles");
        return new HashSet<>(roles);
    }

    public Set<String> addRolesByToken(String accessToken, UserRole role) {
        final Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(accessToken);
        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) claims.getBody().get("roles");
        roles.add(role.getKey());
        return new HashSet<>(roles);
    }

    //---------------------------------------------------------------------------------------------

}
