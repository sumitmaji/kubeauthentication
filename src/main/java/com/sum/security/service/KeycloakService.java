package com.sum.security.service;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;

@Profile("keycloak")
@Service
public class KeycloakService implements AuthService{

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

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", clientId);
        map.add("client_secret", clientSecret);
        map.add("username", username);
        map.add("password", password);
        map.add("scope", "openid profile email groups");
        map.add("grant_type", passwordGrantType);
        map.add("audience", audience);
        map.add("realm", realm);

        String retrieve = "";

        try {
            SslContext sslContext = SslContextBuilder
                    .forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .build();
            HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(sslContext));
            WebClient webClient = WebClient
                    .builder()
                    .clientConnector(new ReactorClientHttpConnector(httpClient))
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .baseUrl(issuerUrl).build();
            retrieve = webClient.post()
                    .uri(tokenEndpoint)
                    .body(BodyInserters.fromFormData(map))
                    .retrieve()
                    .bodyToMono(String.class).block();

        } catch (WebClientResponseException | SSLException e) {
            e.printStackTrace();
            return "{\"error\": \"Error while fetching data\", \"error_description\": \"" + e.getMessage() + "\"}";
        }

        return retrieve;
    }
}
