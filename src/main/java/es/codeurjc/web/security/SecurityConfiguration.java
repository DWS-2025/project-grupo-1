package es.codeurjc.web.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
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
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
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
                        .requestMatchers(HttpMethod.PUT, "/api/sections/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/sections/*/image").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/sections/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/sections/*/image").hasRole("ADMIN")
                        // USERS
                        .requestMatchers(HttpMethod.GET, "/api/users/").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/users/*").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/users/*/image").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/users/*/CV").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/users/*/followings").hasRole("USER")
                        .requestMatchers(HttpMethod.POST, "/api/users/*/image").hasRole("USER")
                        .requestMatchers(HttpMethod.PUT, "/api/users/*").hasRole("USER")
                        .requestMatchers(HttpMethod.PUT, "/api/users/*/image").hasRole("USER")
                        .requestMatchers(HttpMethod.PUT, "/api/users/*/CV").hasRole("USER")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/*").hasRole("USER")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/*/followings").hasRole("USER")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/*/image").hasRole("USER")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/*/CV").hasRole("USER")

                        // POSTS
                        .requestMatchers(HttpMethod.GET, "/api/post/").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/post/*").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/post/*/image").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/post/comments").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/post/*/comments").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/post/*/comments/*").hasRole("USER")
                        .requestMatchers(HttpMethod.POST, "/api/post/").hasRole("USER")
                        .requestMatchers(HttpMethod.POST, "/api/post/*/image").hasRole("USER")
                        .requestMatchers(HttpMethod.POST, "/api/post/*/comments").hasRole("USER")
                        .requestMatchers(HttpMethod.PUT, "/api/post/*").hasRole("USER")
                        .requestMatchers(HttpMethod.PUT, "/api/post/*/image").hasRole("USER")
                        .requestMatchers(HttpMethod.PUT, "/api/post/*/comments/*").hasRole("USER")
                        .requestMatchers(HttpMethod.DELETE, "/api/post/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/post/*/image").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/post/*/comments/*").hasRole("ADMIN")
                        // PUBLIC ENDPOINTS
                        .requestMatchers(HttpMethod.POST, "/api/users/").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                );

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
                // PUBLIC PAGES
                .requestMatchers("/", "/assets/**", "/vendor/**", "/home", "/register",
                        "/login")
                .permitAll()
                .requestMatchers(HttpMethod.GET, "/post", "/section",
                        "/section/{id:[0-9]+}/image", "/user/*/image",
                        "/no-image.png", "/images/spinner.gif",
                        "/post/{id:[0-9]+}", "/post/{id:[0-9]+}/image")
                .permitAll()
                // PRIVATE PAGES
                .requestMatchers(HttpMethod.GET, "/users/admin").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/section/*/edit").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/section/*/edit").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/section/*/delete").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/section/*/delete").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/post/*/edit").hasRole("USER")
                .requestMatchers(HttpMethod.POST, "/post/*/edit").hasRole("USER")
                .requestMatchers(HttpMethod.GET, "/post/*/delete").hasRole("USER")
                .requestMatchers(HttpMethod.POST, "/post/*/delete").hasRole("USER")
                .anyRequest().authenticated())
                .formLogin(formLogin -> formLogin
                .loginPage("/login")
                .failureUrl("/login")
                .defaultSuccessUrl("/discover")
                .permitAll())
                .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .permitAll())
                .exceptionHandling(exception -> exception
                .accessDeniedPage("/error"));

        http
                .headers(headers -> headers
                .contentSecurityPolicy(csp -> csp
                .policyDirectives(
                        "default-src 'self'; "
                        + "script-src 'self' 'unsafe-inline' https://cdn.quilljs.com https://cdn.jsdelivr.net https://unpkg.com; "
                        + "style-src 'self' 'unsafe-inline' https://cdn.quilljs.com https://fonts.googleapis.com https://unpkg.com https://cdn.jsdelivr.net; "
                        + "img-src 'self' data: blob: https:; "
                        + "font-src 'self' https://fonts.gstatic.com data:; "
                        + "connect-src 'self'; "
                        + "frame-src 'none'; "
                        + "object-src 'none'; "
                        + "form-action 'self';")));
        return http.build();
    }
}
