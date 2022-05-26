package com.sum.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@Order(1)
public class OAuth0WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/token").authenticated()
                .and()
                .oauth2Login()
                .and()
                .logout()
                .logoutSuccessUrl("/login")
                .invalidateHttpSession(true).deleteCookies("JSESSIONID");
    }

}
