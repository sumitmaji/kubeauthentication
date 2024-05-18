package com.sum.security.service;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Profile("auth0")
@Service
public class OAuth0Service implements AuthService{

    @Value(value = "${oauth.passwordGrantType}")
    private String passwordGrantType;

    @Value(value = "${oauth.clientId}")
    private String clientId;

    @Value(value = "${oauth.clientSecret}")
    private String clientSecret;

    @Value(value = "${oauth.realm}")
    private String realm;

    @Value(value = "${oauth.audience}")
    private String audience;

    @Value(value = "${oauth.issuerUrl}")
    private String issuerUrl;

    @Value(value = "${oauth.tokenEndpoint}")
    private String tokenEndpoint;


    @Override
    public String fetchToken(String username, String password) {
        System.out.println("Client Id: " + clientId);
        System.out.println("Client Secret: " + clientSecret);
        Payload payload = new Payload(passwordGrantType, username, password, clientId,
                clientSecret, realm, "openid", audience);
        String retrieve = "";
        WebClient webClient = WebClient.create(issuerUrl);
        try {
            retrieve = webClient.post()
                    .uri(tokenEndpoint)
                    .body(Mono.just(payload), Payload.class)
                    .retrieve()
                    .bodyToMono(String.class).block();

        } catch (WebClientResponseException e) {
            return "{\"error\": \"Error while fetching data\", \"error_description\": \"" + e.getMessage() + "\"}";
        }

        return retrieve;
    }

    class Error {
        private String error;
        private String error_description;
    }

    @Data
    class Payload {
        private String grant_type;
        private String username;
        private String password;
        private String client_id;
        private String client_secret;
        private String realm;
        private String scope;
        private String audience;

        public Payload(String grantType, String username, String password,
                       String clientId, String clientSecret, String realm,
                       String openid, String audience) {
            this.grant_type = grantType;
            this.username = username;
            this.password = password;
            this.client_id = clientId;
            this.client_secret = clientSecret;
            this.realm = realm;
            this.scope = openid;
            this.audience = audience;
        }
    }
}
