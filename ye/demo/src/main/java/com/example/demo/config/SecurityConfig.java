package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.jwt.JwtAccessDeniedHandler;
import com.example.demo.jwt.JwtAuthenticationEntryPoint;
import com.example.demo.jwt.JwtSecurityConfig;
import com.example.demo.jwt.TokenProvider;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	private final TokenProvider tokenProvider;
	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	private final JwtAccessDeniedHandler jwtAccessDeniedHandler; 
	
	public SecurityConfig(TokenProvider tokenProvider,
			JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
			JwtAccessDeniedHandler jwtAccessDeniedHandler) {
		this.tokenProvider = tokenProvider;
		this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
		this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Override
	public void configure(WebSecurity web) {
		web
			.ignoring()
			.antMatchers(
					"/h2-console/**"
					, "/favicon.ico"
			);
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.csrf().disable() // Token 방식을 사용하므로 csrf 설정을 disable   
			
			.exceptionHandling() // 예외처리를 위해 만들었던 코드 지정 
			.authenticationEntryPoint(jwtAuthenticationEntryPoint)
			.accessDeniedHandler(jwtAccessDeniedHandler)
			
			.and() // 데이터 확인을 위해 사용하고 있는 h2-console을 위한 설정 추가 
			.headers()
			.frameOptions()
			.sameOrigin()
			
			.and() // 세션을 사용하지 않기 때문에 세션 셜정 Stateless
			.sessionManagement()
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			
			.and() // 이 세가지 api는 Token 없어도 호출 가능
			.authorizeRequests()
			.antMatchers("/api/hello").permitAll()
			.antMatchers("/api/authenticate").permitAll()
			.antMatchers("/api/signup").permitAll()
			.anyRequest().authenticated()
			
			.and() // JwtFilter를 addFilterBefore 메소드로 등록했던 JwtSecurityConfig 클래스도 적용해준다.
			.apply(new JwtSecurityConfig(tokenProvider));
	}
}
