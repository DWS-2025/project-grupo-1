package es.codeurjc.web.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import es.codeurjc.web.service.RepositoryUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
	@Autowired
	public RepositoryUserDetailsService userDetailsService;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder());

		return authProvider;
	}

	@Bean
	@Order(1)
	public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
		http.authenticationProvider(authenticationProvider());

		http.authorizeHttpRequests(authorize -> authorize
				// PRIVATE ENDPOINTS
				.requestMatchers(HttpMethod.GET, "/api/**").hasAnyRole("USER", "ADMIN")
				.requestMatchers(HttpMethod.POST, "/api/**").hasRole("USER")
				.requestMatchers(HttpMethod.PUT, "/api/**").hasRole("USER")
				.requestMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN")

				// PUBLIC ENDPOINTS
				.anyRequest().permitAll());

		// Disable Form login Authentication
		http.formLogin(formLogin -> formLogin.disable());

		// Disable CSRF protection (it is difficult to implement in REST APIs)
		http.csrf(csrf -> csrf.disable());

		// Disable Basic Authentication
		http.httpBasic(httpBasic -> httpBasic.disable());

		// Stateless session
		http.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		// Add JWT Token filter SE DEBE IMPLEMENTAR
		// http.addFilterBefore(jwtRequestFilter,
		// UsernamePasswordAuthenticationFilter.class);

		return http.build();

	}

	@Bean
	@Order(2)
	public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {

		http.authenticationProvider(authenticationProvider());

		http
				.authorizeHttpRequests(authorize -> authorize
						// PUBLIC PAGES, in /assets/** maybe we should just specify the files we need
						.requestMatchers("/", "/assets/**", "/vendor/**").permitAll()
						// PRIVATE PAGES
						.anyRequest().authenticated())
				.formLogin(formLogin -> formLogin
						.loginPage("/login")
						.failureUrl("/loginerror")
						.defaultSuccessUrl("/home")
						.permitAll())
				.logout(logout -> logout
						.logoutUrl("/logout")
						.logoutSuccessUrl("/login")
						.permitAll());

		// Disable CSRF at the moment
		http.csrf(csrf -> csrf.disable());

		return http.build();
	}
}
