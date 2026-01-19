package com.example.bookingmeeting_be.config;

import com.example.bookingmeeting_be.services.CustomerUserDetailsService;
import com.example.bookingmeeting_be.services.JWTFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
//@RequiredArgsConstructor
public class SecurityConfig {
    @Autowired
    private CustomerUserDetailsService userDetailsService;

    @Autowired
    private JWTFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // tạm disable cho nhanh; khi chạy form POST nhiều thì cân nhắc bật lại
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // static
                        .requestMatchers("/admin/assets/**","/assets/**", "/css/**", "/js/**", "/img/**", "/webjars/**", "/error","/favicon.ico").permitAll()

                        // public pages
                                .requestMatchers("/", "/users/home", "/users/login", "/users/register", "/admin/login", "/admin/register")
                                .permitAll()

// users secured
                                .requestMatchers("/users/**")
                                .hasAnyAuthority("ROLE_ADMIN","ROLE_BOOKER","ROLE_ATTENDEE")

                        // api auth public
                        .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()

                        // ===== ADMIN RULES =====
                        // bookings: ADMIN + BOOKER
                        .requestMatchers("/admin/bookings", "/admin/bookings/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_BOOKER")

                        // admin còn lại: chỉ ADMIN
                        .requestMatchers("/admin/**")
                        .hasAuthority("ROLE_ADMIN")

                        // ===== USERS RULES =====
                        // 3 role đều vào được users
                        .requestMatchers("/users/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_BOOKER", "ROLE_ATTENDEE")

                        // api secured
                        .requestMatchers("/api/**").authenticated()

                        .anyRequest().authenticated()
                )

                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


}
