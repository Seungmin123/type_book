package com.muzlive.kitpage.kitpage.config.jwt;

import com.muzlive.kitpage.kitpage.domain.user.repository.TokenLogRepository;
import com.muzlive.kitpage.kitpage.utils.enums.TokenType;
import com.muzlive.kitpage.kitpage.utils.enums.UserRole;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

	private final JwtTokenProvider tokenProvider;


	private final TokenLogRepository tokenLogRepository;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		String jwt = tokenProvider.resolveToken(request);

		if (StringUtils.hasText(jwt) && tokenProvider.validateAccessToken(jwt)) {
			SimpleGrantedAuthority role = this.getRoleAuthority(jwt);

			if(role == null) {
				response.sendError(HttpServletResponse.SC_FORBIDDEN);
				return;
			}

			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(tokenProvider.getDeviceIdByToken(jwt),null, Collections.singleton(role));
			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}

		filterChain.doFilter(request, response);
	}

	private SimpleGrantedAuthority getRoleAuthority(String jwt) {
		Set<String> roles = tokenProvider.getRolesByToken(jwt);
		String deviceId = tokenProvider.getDeviceIdByToken(jwt);

		SimpleGrantedAuthority role = null;
		if(roles.contains(UserRole.ENGINEER.getKey())) {
			role = new SimpleGrantedAuthority(UserRole.ENGINEER.getKey());
		} else if(roles.contains(UserRole.LINKER.getKey()) && (this.validUserToken(deviceId, TokenType.LOGIN) || this.validUserToken(deviceId, TokenType.LOGIN))) {
			role = new SimpleGrantedAuthority(UserRole.LINKER.getKey());
		} else if(roles.contains(UserRole.HALF_LINKER.getKey()) && this.validUserToken(deviceId, TokenType.CHECK_TAG)) {
			role = new SimpleGrantedAuthority(UserRole.HALF_LINKER.getKey());
		} else if(roles.contains(UserRole.GUEST.getKey()) && this.validUserToken(deviceId, TokenType.ACCESS)){
			role = new SimpleGrantedAuthority(UserRole.GUEST.getKey());
		}

		return role;
	}

	// TODO access + refresh + rotation + blacklist
	private boolean validUserToken(String deviceId, TokenType type) {
		return tokenLogRepository.findFirstByDeviceIdAndTypeOrderByCreatedAtDesc(deviceId, type)
			.map(tokenLog -> tokenLog.getCreatedAt().plusDays(1).isAfter(LocalDateTime.now()))
			.orElse(false);
	}

}
