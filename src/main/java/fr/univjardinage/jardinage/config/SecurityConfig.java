package fr.univjardinage.jardinage.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth

                        // Endpoints publics
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()
                        .requestMatchers("/h2-console/**").permitAll()

                        // RBAC : GET /products accessible a tous les utilisateurs authentifies
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/products/**"
                        ).hasAnyRole(
                                "USER",
                                "VENDOR",
                                "MANAGER",
                                "ADMIN"
                        )

                        // RBAC : POST /products accessible a VENDOR, MANAGER, ADMIN
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/products"
                        ).hasAnyRole(
                                "VENDOR",
                                "MANAGER",
                                "ADMIN"
                        )

                        // RBAC : PUT /products accessible a MANAGER et ADMIN et VENDOR
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/v1/products/**"
                        )
                        .hasAnyRole(
                                "VENDOR",
                                "MANAGER",
                                "ADMIN"
                        )

                        // RBAC : DELETE /products accessible uniquement a ADMIN
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/v1/products/**"
                        ).hasRole("ADMIN")

                        // Tout le reste necessite une authentification
                        .anyRequest().authenticated()

                );
        http.exceptionHandling(exception -> exception
                .authenticationEntryPoint((request, response, ex) ->
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED))
                .accessDeniedHandler((request, response, ex) ->
                        response.sendError(HttpServletResponse.SC_FORBIDDEN))
        );

        // Pour H2 Console
        http.headers(headers ->
                headers.frameOptions(frame -> frame.disable())
        );



        return http.build();
    }
}