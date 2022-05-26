package com.sum.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

@Configuration
public class SecurityConfig {

    private ClientRegistration clientRegistration(){
        ClientRegistration build = ClientRegistration.withRegistrationId("auth0")
                .clientId("C3UHISO3z60iF1JLG8L7VPUSWOASrJfO")
                .clientSecret("9BAQYRvX6qdQyFSkT06tm2jbw0TVFZu6wW4BPFaHECtLsE87e0fBv4bF8pnXYWLs")
                .scope("openid", "profile", "email")
                .authorizationUri("https://skmaji.auth0.com/authorize")
                .tokenUri("https://skmaji.auth0.com/oauth/token")
                .userInfoUri("https://skmaji.auth0.com/userinfo")
                .userNameAttributeName("sub")
                .clientName("OAuth0")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUriTemplate("{baseUrl}/{action}/oauth2/code/{registrationId}")
                .jwkSetUri("https://skmaji.auth0.com/.well-known/jwks.json")
                .build();

        return build;

    }

    @Bean
    public ClientRegistrationRepository clientRepository(){
        ClientRegistration clientRegistration = clientRegistration();
        return new InMemoryClientRegistrationRepository(clientRegistration);
    }
}
