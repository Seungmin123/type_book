package com.muzlive.kitpage.kitpage.config.jwt;

import com.muzlive.kitpage.kitpage.config.exception.CommonException;
import com.muzlive.kitpage.kitpage.config.exception.ExceptionCode;
import com.muzlive.kitpage.kitpage.domain.user.repository.MemberRepository;
import com.muzlive.kitpage.kitpage.utils.enums.UserRole;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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

	private static final List<String> ALLOWED_PATHS
		= Arrays.asList("/h2-console", "/actuator/health",
		// v2
		"/v2/api-docs", "/swagger-resources", "/swagger-resources/**", "/configuration/ui", "/configuration/security", "/swagger-ui.html", "/webjars/**",
		// v3
		"/v3/api-docs/**", "/swagger-ui/**", "/doc/api/**",

		// TODO
		"/v1"
	);

	private final JwtTokenProvider tokenProvider;

	private final MemberRepository memberRepository;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		String uri = request.getRequestURI();
		if (ALLOWED_PATHS.stream().anyMatch(uri::startsWith) || uri.endsWith("/health")) {
			filterChain.doFilter(request, response);
		}

		String jwt = tokenProvider.resolveToken(request);

		if (StringUtils.hasText(jwt) && tokenProvider.validateAccessToken(jwt)) {
			String userDeviceId = tokenProvider.getDeviceIdByToken(jwt);
			memberRepository.findByDeviceId(userDeviceId).orElseThrow(() -> new CommonException(ExceptionCode.USER_NOT_FOUND));

			SimpleGrantedAuthority role = null;
			if(StringUtils.hasText(tokenProvider.getEmailByToken(jwt))) {
				role = new SimpleGrantedAuthority(UserRole.LINKER.getKey());
			} else if(StringUtils.hasText(tokenProvider.getSerialNumberByToken(jwt))) {
				role = new SimpleGrantedAuthority(UserRole.HALF_LINKER.getKey());
			} else {
				role = new SimpleGrantedAuthority(UserRole.GUEST.getKey());
			}

			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDeviceId,null, Collections.singleton(role));
			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}

		filterChain.doFilter(request, response);
	}

}
