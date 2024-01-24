package com.donguri.jejudorang.global.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private final JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    private final JwtAuthEntryPoint jwtAuthEntryPoint;

    public SecurityConfig(JwtUserDetailsService jwtUserDetailsService, JwtAuthEntryPoint jwtAuthEntryPoint) {
        this.jwtUserDetailsService = jwtUserDetailsService;
        this.jwtAuthEntryPoint = jwtAuthEntryPoint;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(jwtUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    /*
    * securityFilterChain
    *
    * Defines a filter chain which is capable of being matched against an HttpServletRequest.
    * in order to decide whether it applies to that request.
    *
    * */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Disables CSRF protection
                .httpBasic(AbstractHttpConfigurer::disable) // Configures HTTP Basic authentication
                .formLogin(AbstractHttpConfigurer::disable) // Specifies to support form based authentication

                /*
                 * Allows configuring of Session Management
                 *
                 * Specifies the various session creation policies for Spring Security
                 * STATELESS: Spring Security will never create an HttpSession and it will never use it to obtain the SecurityContext
                 *
                 * */
                .sessionManagement(
                        (sessionManagement) ->
                                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                /*
                 * Allows restricting access based upon the HttpServletRequest using RequestMatcher implementations
                 *
                 * */
                .authorizeHttpRequests(
                        (authorizationManagerRequestMatcherRegistry ->
                                authorizationManagerRequestMatcherRegistry
                                        .requestMatchers("/", "/login", "/signup",
                                                "/trip", "/trip/list/*", "/trip/places",
                                                "/community/chats", "/community/parties",
                                                "/css/**", "/img/**").permitAll()
                                        .anyRequest().authenticated()));

        http.authenticationProvider(authenticationProvider());

        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /*
    * PasswordEncoder
    *
    * Service interface for encoding passwords.
    * The preferred implementation is BCryptPasswordEncoder.
    *
    * > PasswordEncoderFactories: Used for creating PasswordEncoder instances
    * >> createDelegatingPasswordEncoder : creates a DelegatingPasswordEncoder with default mappings.
    * >>> The mappings current are:
    *       bcrypt - BCryptPasswordEncoder (Also used for encoding)
    * */
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
//    }

}
