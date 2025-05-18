package com.example.myshop.config;

import com.example.myshop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity(debug = true)
@RequiredArgsConstructor
public class SecurityConfig {


    private final UserService service;


    @Autowired
    public PasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http

                .authorizeRequests(authorize -> authorize
                                .requestMatchers(new AntPathRequestMatcher("/images/**")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/css/**")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/libs/**")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/webjars/**")).permitAll()
                        .requestMatchers("/", "/home", "/index", "/about", "/help", "/registration", "/searchByProduct/**").permitAll()
                                .requestMatchers("/user/**","/cart/**", "/cartfromcateg/**","/order/**").hasRole("USER")
                                .requestMatchers("/**").hasRole("ADMIN")

//                        .requestMatchers(HttpMethod.POST, "/api/v1/users/*").permitAll()
//                        .requestMatchers(HttpMethod.PUT, "/api/v1/users").hasRole("ADMIN")
//                        .requestMatchers(HttpMethod.DELETE, "/api/v1/users/*").hasRole("ADMIN")
                )
                .csrf().disable()

//                .requestMatchers("/user/**", "/cart/**", "/cartfromcateg/**").hasRole("USER")
//                .requestMatchers("/**").hasRole("ADMIN")
                //.and()
                .formLogin()
                .loginPage("/login").permitAll()
                .loginProcessingUrl("/login")
                .usernameParameter("login")
                .passwordParameter("password")
                .defaultSuccessUrl("/")
                .and()
                .logout()
                .permitAll()
                .logoutSuccessUrl("/login");

        return http.build();
    }

    @Autowired
    protected void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(service).passwordEncoder(passwordEncoder);
    }

}
