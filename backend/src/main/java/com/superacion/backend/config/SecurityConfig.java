package com.superacion.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
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
}