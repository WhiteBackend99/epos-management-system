package com.epos.backend.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import com.epos.backend.security.JwtAuthFilter;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Value("${app.cors.allowed-origins:http://localhost:3000,http://localhost:5173,http://localhost:8080}")
    private List<String> allowedOrigins;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/auth/**").permitAll()
                    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                    .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                    .requestMatchers("/actuator/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN")
                    .requestMatchers("/api/reports/**").hasAnyRole("ADMIN", "OWNER", "MANAGER")
                    .requestMatchers("/api/promos/**").hasAnyRole("ADMIN", "OWNER", "MANAGER")
                    .requestMatchers("/api/categories/**").hasAnyRole("ADMIN", "OWNER", "MANAGER", "WAREHOUSE")
                    .requestMatchers("/api/products/**").hasAnyRole("ADMIN", "OWNER", "MANAGER", "WAREHOUSE")
                    .requestMatchers("/api/suppliers/**").hasAnyRole("ADMIN", "OWNER", "MANAGER", "WAREHOUSE")
                    .requestMatchers("/api/purchases/**").hasAnyRole("ADMIN", "OWNER", "MANAGER", "WAREHOUSE")
                    .requestMatchers("/api/stock-movements/**").hasAnyRole("ADMIN", "OWNER", "MANAGER", "WAREHOUSE")
                    .requestMatchers("/api/customer-members/**").hasAnyRole("ADMIN", "OWNER", "MANAGER", "CASHIER")
                    .requestMatchers("/api/loyalty/**").hasAnyRole("ADMIN", "OWNER", "MANAGER", "CASHIER")
                    .requestMatchers("/api/pos-sales/**").hasAnyRole("ADMIN", "OWNER", "CASHIER")
                    .requestMatchers("/api/sales-returns/**").hasAnyRole("ADMIN", "OWNER", "CASHIER")
                    .requestMatchers("/api/receipts/**").hasAnyRole("ADMIN", "OWNER", "CASHIER")
                    .requestMatchers("/api/cashier-shifts/**").hasAnyRole("ADMIN", "OWNER", "CASHIER")
                    .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        return (HttpServletRequest request) -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedOrigins(allowedOrigins);
            config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
            config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));
            config.setExposedHeaders(List.of("Authorization"));
            config.setAllowCredentials(true);
            config.setMaxAge(3600L);
            return config;
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
}
