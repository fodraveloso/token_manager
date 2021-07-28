package br.com.security.app.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import br.com.security.app.filters.AuthenticationFilter;
import br.com.security.app.filters.AuthorizationFilter;
import lombok.RequiredArgsConstructor;

@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig  extends WebSecurityConfigurerAdapter {

	private final UserDetailsService userDetailsService;

	private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@Value("${api.signupurl}")
	private String signUpUrl;
	@Value("${api.header-name}")
	private String headerName;
	@Value("${api.prefix}")
	private String prefix;
	@Value("${api.secret}")
	private String secret;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.cors().and().csrf().disable().authorizeRequests().antMatchers(HttpMethod.POST, signUpUrl).permitAll()
				.anyRequest().authenticated().and()
				.addFilter(instanceOfAuthenticationFilter())
				.addFilter(instanceOfAuthorizationFilter()).sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		
		final var source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
		return source;
	}

	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
	}
	
	
	private AuthenticationFilter instanceOfAuthenticationFilter() throws Exception {
		
		return new AuthenticationFilter(authenticationManager(), secret, prefix);
	}
	
	private AuthorizationFilter instanceOfAuthorizationFilter() throws Exception {
		
		return AuthorizationFilter.builder()
				.authenticationManager(authenticationManager())
				.userDetailsService(userDetailsService)
				.headerName(headerName)
				.prefix(prefix)
				.secret(secret)
				.build();
	}
}
