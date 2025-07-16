package com.example.candy.security;

import com.example.candy.security.jwt.JwtAuthTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;

@Configuration
@EnableMethodSecurity(prePostEnabled = true) // Enable @PreAuthorize and @PostAuthorize annotations
public class WebSecurityConfig {
    @Value("${app.cors.allowed-origins:http://localhost:3000}")
    private String allowedOrigins;
    @Autowired
    private JwtAuthTokenFilter jwtAuthTokenFilter;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // Add JWT authentication filter
            .addFilterBefore(jwtAuthTokenFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(auth -> auth
                // Public auth endpoints
                .requestMatchers(HttpMethod.POST, "/api/auth/login", "/api/auth/signup", "/api/auth/logout").permitAll()
                // Public book endpoints (all GET requests)
                .requestMatchers(HttpMethod.GET, "/api/books/**").permitAll()
                // Allow Swagger UI and OpenAPI docs
                .requestMatchers(
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/swagger-ui/index.html",
                    "/v3/api-docs/**",
                    "/webjars/**"
                ).permitAll()
                // Everything else requires authentication
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Split comma-separated origins from env/property
        List<String> origins = List.of(allowedOrigins.split(","));
        configuration.setAllowedOrigins(origins);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true); // if you're sending cookies or Authorization headers

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

// PUBLIC ENDPOINTS (for reference/documentation):
// - POST /api/auth/login (login)
// - POST /api/auth/signup (register)
// - GET  /api/books/{id} (getBookDetail)
// - GET  /api/cart (getCart, requires authentication)
//
// These endpoints are configured as public (except /api/cart) in your security config.
//
// If you want to make /api/cart public (not recommended for real apps),
// you would need to update your security config to permitAll() for that path.
//
// The actual controller methods are already public in their respective controllers.
//
// Example controller method signatures:
//
// @PostMapping("/api/auth/login")
// public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest)
//
// @PostMapping("/api/auth/signup")
// public ResponseEntity<?> registerUser(@RequestBody RegisterRequest signUpRequest)
//
// @GetMapping("/api/books/{id}")
// public ResponseEntity<BookResponse> getBookById(@PathVariable Long id)
//
// @GetMapping("/api/cart")
// @PreAuthorize("isAuthenticated()")
// public ResponseEntity<?> getUserCart()
