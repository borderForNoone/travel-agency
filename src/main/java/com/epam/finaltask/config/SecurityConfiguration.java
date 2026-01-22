package com.epam.finaltask.config;

import com.epam.finaltask.model.Permission;
import com.epam.finaltask.jwt.JWTAuthenticationFilter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableMethodSecurity
public class SecurityConfiguration {

    private final AuthenticationProvider authenticationProvider;
    private final JWTAuthenticationFilter jwtAuthenticationFilter;

    // Simple in-memory brute-force protection (for demo, replace with Redis in prod)
    private final Map<String, LoginAttempt> loginAttemptCache = new ConcurrentHashMap<>();
    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCK_TIME_MS = 15 * 60 * 1000; // 15 mins

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // HTTPS
                .requiresChannel(channel -> channel.anyRequest().requiresSecure())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/users/register", "/page/**").permitAll()
                        .requestMatchers("/user/**", "/users/data")
                        .hasAnyAuthority(
                                Permission.USER_READ.name(),
                                Permission.USER_UPDATE.name(),
                                Permission.MANAGER_UPDATE.name(),
                                Permission.ADMIN_READ.name()
                        )
                        .requestMatchers("/manager/**")
                        .hasAnyAuthority(
                                Permission.MANAGER_UPDATE.name(),
                                Permission.ADMIN_UPDATE.name()
                        )
                        .anyRequest()
                        .hasAnyAuthority(
                                Permission.ADMIN_READ.name(),
                                Permission.ADMIN_CREATE.name(),
                                Permission.ADMIN_UPDATE.name(),
                                Permission.ADMIN_DELETE.name()
                        )
                )

                // Stateless session
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                .headers(headers -> headers
                        .contentTypeOptions(contentTypeOptions -> contentTypeOptions.disable())
                        .frameOptions(frame -> frame.sameOrigin())
                        .httpStrictTransportSecurity(hsts -> hsts.includeSubDomains(true).maxAgeInSeconds(31536000))
                        .referrerPolicy(referrer -> referrer
                                .policy(org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
                        )
                        .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'; script-src 'self'"))
                )

                // Custom login check for brute-force protection
                .formLogin(form -> form
                        .loginProcessingUrl("/auth/login")
                        .successHandler((request, response, authentication) -> {
                            String username = authentication.getName();
                            loginAttemptCache.remove(username); // reset attempts on success
                            log.info("User '{}' successfully logged in", username);
                            response.setStatus(200);
                        })
                        .failureHandler((request, response, exception) -> {
                            String username = request.getParameter("username");
                            LoginAttempt attempt = loginAttemptCache.getOrDefault(username, new LoginAttempt());
                            attempt.increment(username);
                            loginAttemptCache.put(username, attempt);

                            if (attempt.isLocked()) {
                                log.warn("User '{}' account locked due to too many failed login attempts", username);
                                response.setStatus(423); // Locked
                            } else {
                                log.warn("Failed login attempt {} for user '{}'", attempt.getAttempts(), username);
                                response.setStatus(401); // Unauthorized
                            }
                        })
                );

        return http.build();
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy(
                "ROLE_ADMIN > ROLE_MANAGER \n ROLE_MANAGER > ROLE_USER"
        );

        return hierarchy;
    }

    @Getter
    @Slf4j
    private static class LoginAttempt {
        private int attempts = 0;
        private long firstAttemptTime = Instant.now().toEpochMilli();

        void increment(String username) {
            long now = Instant.now().toEpochMilli();
            if (now - firstAttemptTime > LOCK_TIME_MS) {
                attempts = 1;
                firstAttemptTime = now;
            } else {
                attempts++;
            }
            log.warn("Login attempt {} for user '{}'", attempts, username);
        }

        boolean isLocked() {
            return attempts >= MAX_ATTEMPTS && (Instant.now().toEpochMilli() - firstAttemptTime) < LOCK_TIME_MS;
        }
    }
}
