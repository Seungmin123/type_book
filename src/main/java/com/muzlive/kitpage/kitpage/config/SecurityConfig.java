package com.muzlive.kitpage.kitpage.config;

import com.muzlive.kitpage.kitpage.config.filter.HttpRequestLoggingFilter;
import com.muzlive.kitpage.kitpage.config.filter.SwaggerFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

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
			.contentSecurityPolicy("script-src 'self'; style-src 'self'; img-src 'self'")
			.and().frameOptions().disable()

			.and()
			.authorizeRequests()
			.antMatchers("/swagger-ui*/**").permitAll()
			.antMatchers("/v3/api-docs/**").permitAll()
			.antMatchers("/webjars/**").permitAll()
			.antMatchers("/actuator/health").permitAll()

			// TODO 권한 수정
			.antMatchers("/v1/**").permitAll()
			.anyRequest().authenticated()
			.and()
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