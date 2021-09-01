package com.presbo.presboservice.configure.security;

import com.presbo.presboservice.security.jwt.JwtSecurityConfigurer;
import com.presbo.presboservice.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.httpBasic().disable().csrf().disable().cors().and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().authorizeRequests()
                .antMatchers("/auth/signin").permitAll().antMatchers("/user/bootstrap","/user/test", "/actuator/**").permitAll()
                .antMatchers("/mobile/**").hasRole("MOBILE")
                .antMatchers("/**").hasRole("USER")
                .antMatchers("/**").permitAll() //Should not be commited or pushed
                .anyRequest().authenticated()
                .and().apply(new JwtSecurityConfigurer(jwtTokenProvider));

    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://18.132.131.55:3500", "http://localhost:3500",
                "http://10.0.0.103:3500", "http://105.224.234.6:3500","http://3.9.113.123:3500","http://3.9.113.123", "https://presbo.io"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type",
                "application/x-www-form-urlencoded", "multipart/form-data"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
