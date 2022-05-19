package com.sum.ldap;

import org.springframework.cglib.proxy.NoOp;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.LdapShaPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/healthz").permitAll()
                .antMatchers("/echo").permitAll()
                .antMatchers("/302").permitAll()
                .antMatchers("/").authenticated()
                .antMatchers("/welcome").authenticated()
                .and()
                .formLogin();
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .ldapAuthentication()
                .userSearchFilter("(cn={0})")
//                .userSearchBase("ou=users")
//                .userDnPatterns("cn={0},ou=users")
//                .groupSearchBase("ou=users")
//                .contextSource(contextSource())
                .contextSource()
                .url("ldap://ldap.default.svc.cloud.uat/dc=default,dc=svc,dc=cloud,dc=uat");
//                .and()
//                .passwordCompare()
//                .passwordEncoder(new LdapShaPasswordEncoder())
//                .passwordAttribute("userpassword");
    }


    @Bean
    public BaseLdapPathContextSource contextSource() {
        LdapContextSource bean = new LdapContextSource();
        bean.setUrl("ldap://ldap.default.svc.cloud.uat");
        bean.setBase("dc=default,dc=svc,dc=cloud,dc=uat");
        bean.setUserDn("cn=admin,dc=default,dc=svc,dc=cloud,dc=uat");
        bean.setPassword("sumit");
        bean.setPooled(true);
        bean.setReferral("follow");
        bean.afterPropertiesSet();
        return bean;
    }

}
