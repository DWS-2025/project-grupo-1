package es.codeurjc.web.security.jwt;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

/**
 * JwtTokenProvider is a Spring component responsible for generating, parsing, and validating JWT tokens
 * for authentication and authorization purposes.
 * <p>
 * It supports extracting JWT tokens from HTTP headers (Authorization: Bearer) and cookies,
 * and provides methods to generate access and refresh tokens for a given user.
 * </p>
 *
 * <ul>
 *   <li>{@link #tokenStringFromHeaders(HttpServletRequest)} - Extracts the JWT token string from the Authorization header.</li>
 *   <li>{@link #tokenStringFromCookies(HttpServletRequest)} - Extracts the JWT token string from cookies.</li>
 *   <li>{@link #validateToken(HttpServletRequest, boolean)} - Validates a JWT token from either header or cookie.</li>
 *   <li>{@link #validateToken(String)} - Validates a JWT token string and returns its claims.</li>
 *   <li>{@link #generateAccessToken(UserDetails)} - Generates a signed JWT access token for the given user.</li>
 *   <li>{@link #generateRefreshToken(UserDetails)} - Generates a signed JWT refresh token for the given user.</li>
 * </ul>
 *
 * <p>
 * The class uses a symmetric secret key for signing and verifying tokens, and stores user roles and token type as claims.
 * </p>
 * 
 * @author Grupo 1
 */
@Component
public class JwtTokenProvider {

	private final SecretKey jwtSecret = Jwts.SIG.HS256.key().build();
	private final JwtParser jwtParser = Jwts.parser().verifyWith(jwtSecret).build();

	public String tokenStringFromHeaders(HttpServletRequest req) {
		String bearerToken = req.getHeader(HttpHeaders.AUTHORIZATION);
		if (bearerToken == null) {
			throw new IllegalArgumentException("Missing Authorization header");
		}
		if (!bearerToken.startsWith("Bearer ")) {
			throw new IllegalArgumentException("Authorization header does not start with Bearer: " + bearerToken);
		}
		return bearerToken.substring(7);
	}

	private String tokenStringFromCookies(HttpServletRequest request) {
		var cookies = request.getCookies();
		if (cookies == null) {
			throw new IllegalArgumentException("No cookies found in request");
		}

		for (Cookie cookie : cookies) {
			if (TokenType.ACCESS.cookieName.equals(cookie.getName())) {
				String accessToken = cookie.getValue();
				if (accessToken == null) {
					throw new IllegalArgumentException(
							"Cookie %s has null value".formatted(TokenType.ACCESS.cookieName));
				}

				return accessToken;
			}
		}
		throw new IllegalArgumentException("No access token cookie found in request");
	}

	public Claims validateToken(HttpServletRequest req, boolean fromCookie) {
		var token = fromCookie ? tokenStringFromCookies(req) : tokenStringFromHeaders(req);
		return validateToken(token);
	}

	public Claims validateToken(String token) {
		return jwtParser.parseSignedClaims(token).getPayload();
	}

	public String generateAccessToken(UserDetails userDetails) {
		return buildToken(TokenType.ACCESS, userDetails).compact();
	}

	public String generateRefreshToken(UserDetails userDetails) {
		var token = buildToken(TokenType.REFRESH, userDetails);
		return token.compact();
	}

	private JwtBuilder buildToken(TokenType tokenType, UserDetails userDetails) {
		var currentDate = new Date();
		var expiryDate = Date.from(new Date().toInstant().plus(tokenType.duration));
		return Jwts.builder()
				.claim("roles", userDetails.getAuthorities())
				.claim("type", tokenType.name())
				.subject(userDetails.getUsername())
				.issuedAt(currentDate)
				.expiration(expiryDate)
				.signWith(jwtSecret);
	}
}
