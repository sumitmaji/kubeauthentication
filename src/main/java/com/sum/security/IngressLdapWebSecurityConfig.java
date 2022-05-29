package com.sum.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@Order(3)
public class IngressLdapWebSecurityConfig extends WebSecurityConfigurerAdapter {


    private AuthenticationSuccessHandler getSuccessHandler() {
        return new IngressLdapSuccessHandler();
    }

    ;

    private AuthenticationFailureHandler getFailureHandler() {
        return new IngressLdapFailureHandler();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .requestMatchers()
                .antMatchers("/check", "/authenticate")
                .and()
                .authorizeRequests()
                .antMatchers("/check").permitAll() // Check if user is authenticated
                .antMatchers("/authenticate").permitAll() // Presents user with login page
                .anyRequest().authenticated()

                .and()
                .formLogin()
                .loginPage("/authenticate")
                .successHandler(getSuccessHandler())
                .failureHandler(getFailureHandler())
                .and()
                .logout()
                .logoutSuccessUrl("/login")
                .invalidateHttpSession(true).deleteCookies("JSESSIONID")
        ;
    }
}
