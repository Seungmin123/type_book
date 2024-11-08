package com.muzlive.kitpage.kitpage.config.jwt;

import com.muzlive.kitpage.kitpage.config.exception.CommonException;
import com.muzlive.kitpage.kitpage.config.exception.ExceptionCode;
import com.muzlive.kitpage.kitpage.domain.user.repository.KitRepository;
import com.muzlive.kitpage.kitpage.domain.user.repository.MemberRepository;
import com.muzlive.kitpage.kitpage.utils.enums.UserRole;
import java.io.IOException;
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

	private final MemberRepository memberRepository;

	private final KitRepository kitRepository;

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
		} else if(roles.contains(UserRole.LINKER.getKey())) {
			role = memberRepository.findByDeviceIdAndEmail(deviceId, tokenProvider.getEmailByToken(jwt))
				.map(member -> new SimpleGrantedAuthority(UserRole.LINKER.getKey()))
				.orElse(null);
		} else if(roles.contains(UserRole.HALF_LINKER.getKey())) {
			role = kitRepository.findByDeviceIdAndSerialNumber(deviceId, tokenProvider.getSerialNumberByToken(jwt))
				.map(kit -> new SimpleGrantedAuthority(UserRole.HALF_LINKER.getKey()))
				.orElse(null);
		} else if(roles.contains(UserRole.GUEST.getKey())){
			role = memberRepository.findByDeviceId(deviceId)
				.map(member -> new SimpleGrantedAuthority(UserRole.GUEST.getKey()))
				.orElse(null);
		}

		return role;
	}

}
