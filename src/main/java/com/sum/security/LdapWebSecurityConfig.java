package com.sum.security;

import com.sum.security.filters.KubernetesAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@Order(2)
public class LdapWebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/healthz").permitAll()
                .antMatchers("/kubectl").permitAll()
                .antMatchers("/echo").permitAll()
                .antMatchers("/302").permitAll()
                .antMatchers("/").authenticated()
                .antMatchers("/kubeauth").permitAll()
                .antMatchers("/welcome").authenticated()
                .and()
                .formLogin()
                .and()
                .logout()
                .logoutSuccessUrl("/login")
                .invalidateHttpSession(true).deleteCookies("JSESSIONID");

        http.addFilterBefore(new KubernetesAuthFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .ldapAuthentication()
                .userSearchFilter("(cn={0})")
                .contextSource(contextSource());
    }


    @Bean
    public BaseLdapPathContextSource contextSource() {
        LdapContextSource bean = new LdapContextSource();
        bean.setUrl("ldap://ldap.default.svc.cloud.uat");
        bean.setBase("dc=default,dc=svc,dc=cloud,dc=uat");
        bean.setUserDn("cn=admin,dc=default,dc=svc,dc=cloud,dc=uat");
        bean.setPassword("sumit");
        bean.setPooled(true);
        bean.setAnonymousReadOnly(false);
        bean.afterPropertiesSet();
        return bean;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}
