package com.sum.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class AuthenticationApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthenticationApplication.class, args);
	}

	/**
	 * This creates a webclient bean which is configured to call a resource api
	 * with access token
	 * @param clientRegistrationRepository
	 * @param oAuth2AuthorizedClientRepository
	 * @return
	 */
	@Bean
	public WebClient webClient(ClientRegistrationRepository clientRegistrationRepository,
							   OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository){
		ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2 =
				new ServletOAuth2AuthorizedClientExchangeFilterFunction(clientRegistrationRepository,
						oAuth2AuthorizedClientRepository);
		oauth2.setDefaultOAuth2AuthorizedClient(true);
		return WebClient.builder().apply(
				oauth2.oauth2Configuration()
		).build();
	}

}
