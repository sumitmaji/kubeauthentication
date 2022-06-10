package com.sum.security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

/**
 * Auth0 openid connect details
 */
@Configuration
public class SecurityConfig {

    @Value(value = "${oauth.registrationId}")
    private String registrationId;

    @Value(value = "${oauth.clientId}")
    private String clientId;

    @Value(value = "${oauth.clientSecret}")
    private String clientSecret;

    @Value(value = "${oauth.authorizeUrl}")
    private String authorizeUrl;

    @Value(value = "${oauth.tokenUrl}")
    private String tokenUrl;

    @Value(value = "${oauth.userInfoUrl}")
    private String userInfoUrl;

    @Value(value = "${oauth.clientName}")
    private String clienName;

    @Value(value = "${oauth.jwkUrl}")
    private String jwkUrl;


    private ClientRegistration clientRegistration(){
        System.out.println(clientId);
        System.out.println(clientSecret);
        ClientRegistration build = ClientRegistration.withRegistrationId(registrationId)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .scope("openid", "profile", "email")
                .authorizationUri(authorizeUrl)
                .tokenUri(tokenUrl)
                .userInfoUri(userInfoUrl)
                .userNameAttributeName("sub")
                .clientName(clienName)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUriTemplate("{baseUrl}/{action}/oauth2/code/{registrationId}")
                .jwkSetUri(jwkUrl)
                .build();

        return build;

    }

    @Bean
    public ClientRegistrationRepository clientRepository(){
        ClientRegistration clientRegistration = clientRegistration();
        return new InMemoryClientRegistrationRepository(clientRegistration);
    }
}
