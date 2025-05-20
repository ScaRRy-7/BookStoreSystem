package com.ifellow.bookstore.configuration;

import com.ifellow.bookstore.enumeration.RoleName;
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
                        .requestMatchers(HttpMethod.POST, "api/genres/**").hasAuthority(RoleName.ROLE_MANAGER.name())

                        .requestMatchers(HttpMethod.GET, "/api/books/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/books/**").hasAuthority(RoleName.ROLE_MANAGER.name())

                        .requestMatchers(HttpMethod.GET, "/api/authors/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/authors/**").hasAuthority(RoleName.ROLE_MANAGER.name())

                        .requestMatchers(HttpMethod.POST, "/api/stores/{storeId}/sales").hasAuthority(RoleName.ROLE_CLIENT.name())
                        .requestMatchers(HttpMethod.GET, "/api/sales/**").hasAnyAuthority(RoleName.ROLE_MANAGER.name(), RoleName.ROLE_ADMIN.name())

                        .requestMatchers(HttpMethod.POST, "/api/warehouses/{warehouseId}/orders").hasAuthority(RoleName.ROLE_CLIENT.name())
                        .requestMatchers(HttpMethod.POST, "/api/orders/{orderId}/cancel").hasAnyAuthority(RoleName.ROLE_CLIENT.name(), RoleName.ROLE_MANAGER.name())
                        .requestMatchers(HttpMethod.POST, "/api/orders/{orderId}/complete").hasAuthority(RoleName.ROLE_MANAGER.name())
                        .requestMatchers(HttpMethod.GET, "/api/orders/**").hasAnyAuthority(RoleName.ROLE_MANAGER.name(), RoleName.ROLE_ADMIN.name())

                        .requestMatchers(HttpMethod.POST, "/api/stores").hasAuthority(RoleName.ROLE_ADMIN.name())
                        .requestMatchers(HttpMethod.POST, "/api/stores/{storeId}/stock/**").hasAuthority(RoleName.ROLE_MANAGER.name())
                        .requestMatchers(HttpMethod.GET, "/api/stores/**").permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/transfer/**").hasAuthority(RoleName.ROLE_MANAGER.name())

                        .requestMatchers(HttpMethod.GET, "/api/warehouses/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/warehouses/{warehouseId}/stock**").hasAuthority(RoleName.ROLE_MANAGER.name())
                        .requestMatchers(HttpMethod.POST, "/api/warehouses").hasAuthority(RoleName.ROLE_ADMIN.name())

                        .requestMatchers("/api/adminpanel/**").hasAuthority(RoleName.ROLE_ADMIN.name())


                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(((_, response, _) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")))
                        .accessDeniedHandler(((_, response, _) ->
                                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden")))
                )
                .addFilterAfter(jwtAuthenticationFilter, LogoutFilter.class)
                .build();
    }
}
