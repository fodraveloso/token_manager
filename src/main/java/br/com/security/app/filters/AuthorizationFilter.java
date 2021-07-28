package br.com.security.app.filters;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.StringUtils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import lombok.Builder;

public class AuthorizationFilter extends BasicAuthenticationFilter {

	private final UserDetailsService userDetailsService;
	private final String headerName;
	private final String prefix;
	private final String secret;

	@Builder
	private AuthorizationFilter(AuthenticationManager authenticationManager, UserDetailsService userDetailsService,
			String headerName, String prefix, String secret) {
		super(authenticationManager);
		this.userDetailsService = userDetailsService;
		this.headerName = headerName;
		this.prefix = prefix;
		this.secret = secret;
	}

	@Override
	protected void doFilterInternal(final HttpServletRequest req, final HttpServletResponse res,
			final FilterChain chain) throws IOException, ServletException {

		String header = req.getHeader(headerName);

		if (StringUtils.hasLength(header)) {

			UsernamePasswordAuthenticationToken authentication = getAuthentication(req);
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}
		chain.doFilter(req, res);
	}

	private UsernamePasswordAuthenticationToken getAuthentication(final HttpServletRequest req) {

		String token = req.getHeader(headerName);
		return getPrincipal(token);
	}

	private UsernamePasswordAuthenticationToken getPrincipal(final String token) {

		return Optional.ofNullable(token)
				.map(value -> createToken(JWT.require(Algorithm.HMAC256(secret.getBytes())).build()
						.verify(token.replace(prefix, "")).getSubject()))
				.orElse(null);
	}

	private UsernamePasswordAuthenticationToken createToken(final String user) {

		return Optional.ofNullable(user).map(value -> new UsernamePasswordAuthenticationToken(value, null,
				userDetailsService.loadUserByUsername(user).getAuthorities())).orElse(null);
	}
}
