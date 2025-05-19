package es.codeurjc.web.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Configuration class that registers the {@link CSRFHandlerInterceptor} as a Spring MVC interceptor.
 * This ensures that CSRF protection logic is applied to incoming HTTP requests.
 * Implements {@link WebMvcConfigurer} to customize the application's MVC configuration.
 * 
 * @author Grupo 1
 */
@Configuration
public class CSRFHandlerConfiguration implements WebMvcConfigurer {

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new CSRFHandlerInterceptor());
	}
}

/**
 * Interceptor that adds the CSRF token to the model after the handler is executed.
 * <p>
 * This interceptor retrieves the {@link CsrfToken} from the request attributes and,
 * if present, adds its value to the model under the attribute name "token".
 * This allows views to access the CSRF token for use in forms and AJAX requests.
 * </p>
 * 
 * @author Grupo 1
 */
class CSRFHandlerInterceptor implements HandlerInterceptor {

	@Override
	public void postHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler,
			final ModelAndView modelAndView) throws Exception {

		if (modelAndView != null) {

			CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
			if (token != null) {
				modelAndView.addObject("token", token.getToken());
			}
		}

	}

}
