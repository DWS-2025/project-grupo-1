package es.codeurjc.web.restController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.codeurjc.web.security.jwt.AuthResponse.Status;
import es.codeurjc.web.security.jwt.AuthResponse;
import es.codeurjc.web.security.jwt.LoginRequest;
import es.codeurjc.web.security.jwt.UserLoginService;
import jakarta.servlet.http.HttpServletResponse;

/**
 * REST controller for handling authentication-related endpoints.
 * <p>
 * Provides endpoints for user login, token refresh, and logout operations.
 * </p>
 *
 * <ul>
 *   <li><b>/api/auth/login</b>: Authenticates a user and returns authentication tokens.</li>
 *   <li><b>/api/auth/refresh</b>: Refreshes the authentication token using a refresh token.</li>
 *   <li><b>/api/auth/logout</b>: Logs out the user and invalidates the authentication session.</li>
 * </ul>
 *
 * @author Grupo 1
 */
@RestController
@RequestMapping("/api/auth")
public class LoginController {

	@Autowired
	private UserLoginService userService;

	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(
			@RequestBody LoginRequest loginRequest,
			HttpServletResponse response) {

		return userService.login(response, loginRequest);
	}

	@PostMapping("/refresh")
	public ResponseEntity<AuthResponse> refreshToken(
			@CookieValue(name = "RefreshToken", required = false) String refreshToken, HttpServletResponse response) {

		return userService.refresh(response, refreshToken);
	}

	@PostMapping("/logout")
	public ResponseEntity<AuthResponse> logOut(HttpServletResponse response) {
		return ResponseEntity.ok(new AuthResponse(Status.SUCCESS, userService.logout(response)));
	}

}
