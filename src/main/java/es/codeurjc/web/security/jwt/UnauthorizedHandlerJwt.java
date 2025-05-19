package es.codeurjc.web.security.jwt;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * This class implements {@link AuthenticationEntryPoint} to handle unauthorized access attempts
 * in JWT-based authentication scenarios. When an unauthenticated user tries to access a protected
 * resource, this handler is triggered to log the unauthorized attempt and send a 401 Unauthorized
 * HTTP response with a custom error message containing the exception message and the request path.
 *
 * <p>
 * Usage: Register this component in your Spring Security configuration to customize the response
 * for unauthorized requests.
 * </p>
 *
 * @author Grupo 1
 */
@Component
public class UnauthorizedHandlerJwt implements AuthenticationEntryPoint {

  private static final Logger logger = LoggerFactory.getLogger(UnauthorizedHandlerJwt.class);

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
      throws IOException {
    logger.info("Unauthorized error: {}", authException.getMessage());

    response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
        "message: %s, path: %s".formatted(authException.getMessage(), request.getServletPath()));
  }

}
