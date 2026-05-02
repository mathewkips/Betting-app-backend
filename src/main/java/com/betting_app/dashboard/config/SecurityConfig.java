package com.betting_app.dashboard.config;

import com.betting_app.dashboard.admin.service.AdminService;
import com.betting_app.dashboard.user.security.JwtAuthenticationFilter;
import com.betting_app.dashboard.user.service.AppUserDetailsService;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final AdminService adminService;
    private final AppUserDetailsService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(AdminService adminService,
                          AppUserDetailsService userService,
                          PasswordEncoder passwordEncoder,
                          JwtAuthenticationFilter jwtFilter) {
        this.adminService = adminService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public DaoAuthenticationProvider adminAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(adminService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public DaoAuthenticationProvider userAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(
                adminAuthenticationProvider(),
                userAuthenticationProvider()
        );
    }
    
    
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
    

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//       
    	return http
    	        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
    	        .csrf(csrf -> csrf.disable())
    	        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    	        .authorizeHttpRequests(auth -> auth
    	                .requestMatchers(
    	                        "/",
    	                        "/api/auth/**",
    	                        "/api/admin/login",
    	                        "/api/tips/free",
    	                        "/api/tips/results",
    	                        "/api/subscriptions/plans",
    	                        "/api/payments/paystack/webhook"

    	                ).permitAll()
    	                .requestMatchers("/api/super-admin/**").hasRole("SUPER_ADMIN")
    	                .requestMatchers("/api/admin/**").hasAnyRole("ADMIN", "SUPER_ADMIN")
    	                .requestMatchers(
    	                        "/api/user/**",
    	                        "/api/tips/premium",
    	                        "/api/payments/paystack/initialize",
    	                        "/api/payments/paystack/verify/**",
    	                        "/api/subscriptions/**"
    	                ).hasRole("USER")
    	                .anyRequest().authenticated()
    	        )
    	        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
    	        .build();
    }
}