package com.superacion.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Permite que la app web (otro origen) llame a la API
                .cors(Customizer.withDefaults())
                // API REST sin formularios ni cookies: no se necesita CSRF
                .csrf(csrf -> csrf.disable())
                // No se guarda sesión en el servidor: cada petición trae su token
                .sessionManagement(sesion -> sesion.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(rutas -> rutas
                        // Rutas públicas: registrarse e iniciar sesión
                        .requestMatchers(HttpMethod.POST, "/api/usuarios", "/api/auth/login").permitAll()
                        .requestMatchers("/error").permitAll()
                        // Todo lo demás requiere token
                        .anyRequest().authenticated()
                )
                // Validar el token JWT que llega en el encabezado Authorization
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }

    // Orígenes (direcciones web) autorizados a llamar a la API
    @Bean
    public CorsConfigurationSource corsConfigurationSource(@Value("${app.cors.origenes}") List<String> origenes) {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(origenes);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource fuente = new UrlBasedCorsConfigurationSource();
        fuente.registerCorsConfiguration("/api/**", config);
        return fuente;
    }
}