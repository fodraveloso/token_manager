package br.com.security.app.filters;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.security.app.models.AuthRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RequiredArgsConstructor
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private final AuthenticationManager authenticationManager;
	private final String secret;
	private final String prefix;

	@Override
	@SneakyThrows
	public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) {

		AuthRequest request = new ObjectMapper().readValue(req.getInputStream(), AuthRequest.class);
		return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(),
				request.getPassword(), new ArrayList<>()));
	}

	@Override
	@SneakyThrows
	protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain,
			Authentication auth) {

		String username = ((UserDetails) auth.getPrincipal()).getUsername();

		String token = JWT.create().withSubject(username)
				.withExpiresAt(Date.from(Instant.now().plus(24, ChronoUnit.HOURS)))
				.withClaim("role", "")
				.sign(Algorithm.HMAC256(secret.getBytes()));

		res.getWriter().write(String.format("%s%s", prefix, token));
		res.getWriter().flush();
	}
}
