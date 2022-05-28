package com.sum.security;

import com.sum.security.filters.KubernetesAuthFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@Order(3)
public class IngressLdapWebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .requestMatchers()
                .antMatchers("/authorize", "/authenticate")
                .and()
                .authorizeRequests()
                .antMatchers("/authorize").authenticated() //health api
                .antMatchers("/authenticate").permitAll() //health api
                .antMatchers("/login_user").permitAll() //health api
                .anyRequest().authenticated()

                .and()
                .formLogin()
                .loginPage("/authenticate")
                .loginProcessingUrl("/login_user")
                .and()
                .logout()
                .logoutSuccessUrl("/login")
                .invalidateHttpSession(true).deleteCookies("JSESSIONID")
        ;
    }
}
