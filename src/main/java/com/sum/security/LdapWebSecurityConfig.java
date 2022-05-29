package com.sum.security;

import com.sum.security.filters.KubernetesAuthFilter;
import io.netty.util.internal.NoOpTypeParameterMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.proxy.NoOp;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Thsi web security configuration is only applied for
 * /healthz, /kubeauth, /login, /logout
 */
@Configuration
@Order(2)
public class LdapWebSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * Not used
     */
    @Autowired
    private AuthEntryPointLdap unauthorizedHandler;


    /**
     * It configures the authentication rules. It adds Filter for authentication
     * for kubernetes via proxy.
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .requestMatchers()
                .antMatchers("/healthz", "/kubeauth", "/login", "/logout")
                .and()
                .authorizeRequests()
                .antMatchers("/kubeauth").permitAll() //authentication logic present in the api
                .antMatchers("/healthz").permitAll() //health api
                .and()
                .formLogin()
                .and()
                .logout()
                .logoutSuccessUrl("/login")
                .invalidateHttpSession(true).deleteCookies("JSESSIONID")
        ;

        http.addFilterBefore(new KubernetesAuthFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .ldapAuthentication()
                .userSearchFilter("(cn={0})") // username which would be search in ldap. cn contains username
                .contextSource(contextSource());
    }

    /**
     * Ldap Configuration
     * @return
     */

    @Bean
    public BaseLdapPathContextSource contextSource() {
        LdapContextSource bean = new LdapContextSource();
        bean.setUrl("ldap://ldap.default.svc.cloud.uat"); // Url where ldap application is running
        bean.setBase("dc=default,dc=svc,dc=cloud,dc=uat"); // Base DN
        bean.setUserDn("cn=admin,dc=default,dc=svc,dc=cloud,dc=uat"); // Admin User DN
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

    //Local authentication testing
//
//    @Bean
//    PasswordEncoder passwordEncoder(){
//        return NoOpPasswordEncoder.getInstance();
//    }
//
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.inMemoryAuthentication()
//                .withUser("sumit")
//                .password(passwordEncoder().encode("sumit")).roles("USER");
//    }
//
//    @Bean
//    public UserDetailsService userDetailsServiceBean() throws Exception {
//        return super.userDetailsServiceBean();
//    }
}
