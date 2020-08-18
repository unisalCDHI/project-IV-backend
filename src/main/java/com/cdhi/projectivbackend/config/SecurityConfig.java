package com.cdhi.projectivbackend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private Environment env;

    private static final String[] PUBLIC_MATCHERS = {
            "/h2-console/**",
            "/v2/api-docs",
            "/configuration/ui",
            "/swagger-resources/**",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**"
    };

    private static final String[] PUBLIC_MATCHERS_POST = {
            "/login/**"
    };

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable();
        if (Arrays.asList(env.getActiveProfiles()).contains("test")) {
            http.headers().frameOptions().disable();
            http.authorizeRequests()
                    .anyRequest().permitAll();
            //TODO
            //Permite acessar todos os endpoints quando em perfil 'test'
        }
        else {
            http.authorizeRequests()
                    .antMatchers(PUBLIC_MATCHERS).permitAll()
                    .antMatchers(HttpMethod.POST, PUBLIC_MATCHERS_POST).permitAll()
                    .anyRequest().authenticated();
        }
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        CorsConfiguration config = new CorsConfiguration();
        source.registerCorsConfiguration("/**", config.applyPermitDefaultValues());
        config.setExposedHeaders(Arrays.asList("Authorization", "Id"));
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        return source;
    }
}
