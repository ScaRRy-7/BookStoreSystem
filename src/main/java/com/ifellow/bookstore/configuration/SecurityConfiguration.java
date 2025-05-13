package com.ifellow.bookstore.configuration;

import com.ifellow.bookstore.filter.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        authenticationProvider.setUserDetailsService(userDetailsService);
        return authenticationProvider;

    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService) {
        return new ProviderManager(authenticationProvider(userDetailsService));
    }

    @Bean
    public SecurityFilterChain SecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/api/genres/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "api/genres/**").hasRole("MANAGER") //роли лучше задать в отдельном enam-е, просто чтобы не опечататься в следующий раз

                        .requestMatchers(HttpMethod.GET, "/api/books/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/books/**").hasRole("MANAGER")

                        .requestMatchers(HttpMethod.GET, "/api/authors/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/authors/**").hasRole("MANAGER")

                        .requestMatchers(HttpMethod.POST, "/api/stores/{storeId}/sales").hasRole("CLIENT")
                        .requestMatchers(HttpMethod.GET, "/api/sales/**").hasAnyRole("MANAGER", "ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/warehouses/{warehouseId}/orders").hasRole("CLIENT")
                        .requestMatchers(HttpMethod.POST, "/api/orders/{orderId}/cancel").hasAnyRole("CLIENT", "MANAGER")
                        .requestMatchers(HttpMethod.POST, "/api/orders/{orderId}/complete").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/orders/**").hasAnyRole("MANAGER", "ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/stores").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/stores/{storeId}/stock/**").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/stores/**").permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/transfer/**").hasRole("MANAGER")

                        .requestMatchers(HttpMethod.GET, "/api/warehouses/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/warehouses/{warehouseId}/stock**").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.POST, "/api/warehouses").hasRole("ADMIN")

                        .requestMatchers("/api/adminpanel/**").hasRole("ADMIN")


                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(((request, response, authException) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")))
                        .accessDeniedHandler(((request, response, accessDeniedException) ->
                                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden")))
                )
                .addFilterAfter(jwtAuthenticationFilter, LogoutFilter.class)
                .build();
    }
}
