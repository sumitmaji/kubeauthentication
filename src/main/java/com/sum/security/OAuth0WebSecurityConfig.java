package com.sum.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * This security configuration is applied for any api having oauth2 in the url.
 */
@Configuration
@Order(1)
public class OAuth0WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .requestMatchers()
                .antMatchers("/**/oauth2/**", "/welcome")
                .and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/oauth2/token", "/welcome").authenticated()
                .and()
                .oauth2Login()
                .and()
                .logout()
                .logoutSuccessUrl("/oauth2/token")
                .invalidateHttpSession(true).deleteCookies("JSESSIONID");
    }

}
