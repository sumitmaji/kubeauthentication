package com.sum.security;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@RestController
public class KubeController {

    private String grantType = "http://auth0.com/oauth/grant-type/password-realm";

    @Value(value = "${oauth.clientId}")
    private String clientId;

    @Value(value = "${oauth.clientSecret}")
    private String clientSecret;

    @Value(value = "${oauth.realm}")
    private String realm;

    @Value(value = "${oauth.audience}")
    private String audience;

    @GetMapping("/kubectl")
    public String getToken(@RequestParam String username, @RequestParam String password) {
        Payload payload = new Payload(grantType, username, password, clientId,
                clientSecret, realm, "openid", audience);
        String retrieve = "";
        WebClient webClient = WebClient.create("https://skmaji.auth0.com");
        try {
            retrieve = webClient.post()
                    .uri("/oauth/token")
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
