package com.muzlive.kitpage.kitpage.config;

import com.muzlive.kitpage.kitpage.config.jwt.JwtFilter;
import com.muzlive.kitpage.kitpage.config.swagger.SwaggerFilter;
import com.muzlive.kitpage.kitpage.utils.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

	private final JwtFilter jwtFilter;

	private final SwaggerFilter swaggerFilter;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.httpBasic().disable()
			.csrf().disable()
			.formLogin().disable()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

			.and()
			.headers()
			.contentSecurityPolicy("default-src 'self'; img-src 'self' data:;")
			.and().frameOptions().disable()

			.and()
			.authorizeRequests()
			// TODO 권한 수정
			.antMatchers("/**").permitAll()
			// Health Check
			.antMatchers("/actuator/health").permitAll()
			// swagger v2
			.antMatchers("/v2/api-docs", "/swagger-resources", "/swagger-resources/**", "/configuration/ui", "/configuration/security", "/swagger-ui.html", "/webjars/**").permitAll()
			// swagger v3
			.antMatchers("/v3/api-docs/**", "/swagger-ui/**", "/doc/api/**", "/swagger-url").permitAll()
			// get token
			.antMatchers("/v1/user/token", "/v1/user/checkTag").permitAll()
			// 루트, 에러 경로
			.antMatchers("/", "/error").permitAll()
			.anyRequest().authenticated()
			.and()
			.anonymous().authorities(UserRole.GUEST.getKey())
			.and()
			.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
			.addFilterBefore(swaggerFilter, FilterSecurityInterceptor.class);

		return http.build();
	}

	@Bean
	public HttpFirewall allowUrlEncodedPercentHttpFirewall() {
		StrictHttpFirewall firewall = new StrictHttpFirewall();
		firewall.setAllowUrlEncodedPercent(true);
		return firewall;
	}

	@Bean
	public FilterRegistrationBean<HttpRequestLoggingFilter> filterRegistrationBean() {
		return new FilterRegistrationBean<HttpRequestLoggingFilter>(new HttpRequestLoggingFilter());
	}
}