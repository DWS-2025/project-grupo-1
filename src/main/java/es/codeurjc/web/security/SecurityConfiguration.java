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

import es.codeurjc.web.security.jwt.JwtRequestFilter;
import es.codeurjc.web.security.jwt.UnauthorizedHandlerJwt;
import es.codeurjc.web.service.RepositoryUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    public RepositoryUserDetailsService userDetailsService;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

	@Autowired
	private UnauthorizedHandlerJwt unauthorizedHandlerJwt;

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

    // ¡¡¡¡HAY QUE CONFIRMAR LOS ROLES DE CADA UNO!!!!!
    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http.authenticationProvider(authenticationProvider());

        http
                .securityMatcher("/api/**")
				.exceptionHandling(handling -> handling.authenticationEntryPoint(unauthorizedHandlerJwt));

		http
                .authorizeHttpRequests(authorize -> authorize
                // PRIVATE ENDPOINTS
                // SECTIONS
                .requestMatchers(HttpMethod.GET, "/api/sections/").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/sections/*").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/sections/*/image").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/sections/*").hasRole("USER")
                .requestMatchers(HttpMethod.POST, "/api/sections/*/image").hasRole("USER")
                .requestMatchers(HttpMethod.PUT, "/api/sections/*").hasRole("USER")
                .requestMatchers(HttpMethod.PUT, "/api/sections/*/image").hasRole("USER")
                .requestMatchers(HttpMethod.DELETE, "/api/sections/*").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/sections/*/image").hasRole("ADMIN")
                // USERS
                .requestMatchers(HttpMethod.GET, "/api/users/").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/users/*").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/users/*/image").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/users").hasRole("USER")
                .requestMatchers(HttpMethod.POST, "/api/users/*/followings").hasRole("USER")
                .requestMatchers(HttpMethod.POST, "/api/users/*/image").hasRole("USER")
                .requestMatchers(HttpMethod.POST, "/api/users/*/followings").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/users/*").hasRole("USER")
                .requestMatchers(HttpMethod.PUT, "/api/users/*/image").hasRole("USER")
                .requestMatchers(HttpMethod.DELETE, "/api/users/*/followings").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/users/*/image").hasRole("ADMIN")
                // POSTS
                .requestMatchers(HttpMethod.GET, "/api/posts/").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/posts/*").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/posts/*/image").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/posts/comments").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/posts/*/comments").hasRole("USER")
                .requestMatchers(HttpMethod.GET, "/api/posts/*/comments/*").hasRole("USER")
                .requestMatchers(HttpMethod.POST, "/api/posts/").hasRole("USER")
                .requestMatchers(HttpMethod.POST, "/api/posts/*/image").hasRole("USER")
                .requestMatchers(HttpMethod.POST, "/api/posts/*/comments").hasRole("USER")
                .requestMatchers(HttpMethod.PUT, "/api/posts/*").hasRole("USER")
                .requestMatchers(HttpMethod.PUT, "/api/posts/*/image").hasRole("USER")
                .requestMatchers(HttpMethod.PUT, "/api/posts/*/comments/*").hasRole("USER")
                .requestMatchers(HttpMethod.DELETE, "/api/posts/*").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/posts/*/image").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/posts/*/comments/*").hasRole("ADMIN")
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

        // Add JWT Token filter
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();

    }

    @Bean
    @Order(2)
    public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {

        http.authenticationProvider(authenticationProvider());

        http
                .securityMatcher("/**")
                .authorizeHttpRequests(authorize -> authorize
                // PUBLIC PAGES, in /assets/** maybe we should just specify the files we need
                .requestMatchers("/", "/assets/**", "/vendor/**").permitAll()
                // PRIVATE PAGES
                .anyRequest().authenticated())
                .formLogin(formLogin -> formLogin
                .loginPage("/login")
                .failureUrl("/login")
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