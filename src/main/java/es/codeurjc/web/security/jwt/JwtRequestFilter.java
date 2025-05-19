package es.codeurjc.web.security.jwt;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * JwtRequestFilter is a Spring Security filter that intercepts HTTP requests to validate JWT tokens.
 * <p>
 * This filter extracts the JWT token from the incoming request, validates it using the JwtTokenProvider,
 * and loads the corresponding user details using the UserDetailsService. If the token is valid, it sets
 * the authentication in the SecurityContext, allowing secured endpoints to be accessed by authenticated users.
 * </p>
 * <p>
 * Any exceptions during token validation or authentication are logged, and the filter chain continues processing.
 * </p>
 *
 * Dependencies:
 * <ul>
 *   <li>{@link UserDetailsService} for loading user-specific data.</li>
 *   <li>{@link JwtTokenProvider} for validating and parsing JWT tokens.</li>
 * </ul>
 *
 * Extends:
 * <ul>
 *   <li>{@link OncePerRequestFilter} to ensure the filter is executed once per request.</li>
 * </ul>
 * 
 * @author Grupo 1
 */
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

	private static final Logger log = LoggerFactory.getLogger(JwtRequestFilter.class);

	private final UserDetailsService userDetailsService;

	private final JwtTokenProvider jwtTokenProvider;

	public JwtRequestFilter(UserDetailsService userDetailsService, JwtTokenProvider jwtTokenProvider) {
		this.userDetailsService = userDetailsService;
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		try {
			var claims = jwtTokenProvider.validateToken(request, true);
			var userDetails = userDetailsService.loadUserByUsername(claims.getSubject());

			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
					userDetails, null, userDetails.getAuthorities());

			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(authentication);
		} catch (Exception ex) {
			log.error("Exception processing JWT Token: ", ex);
		}

		filterChain.doFilter(request, response);
	}

}
