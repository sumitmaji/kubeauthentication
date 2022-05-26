package com.sum.security;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JwtController {

    @Autowired
    OAuth2AuthorizedClientService clientService;

    @GetMapping("/token")
    public Token getAuth(@AuthenticationPrincipal OidcUser principal){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        OAuth2AuthorizedClient oAuth2AuthorizedClient = clientService.
                loadAuthorizedClient(token.getAuthorizedClientRegistrationId(), token.getName());
        OidcIdToken idToken = principal.getIdToken();
        String tokenValue = idToken.getTokenValue();


        Token res = new Token(principal.getIdToken().getTokenValue(),
                oAuth2AuthorizedClient.getAccessToken().getTokenValue(),
                oAuth2AuthorizedClient.getRefreshToken() != null ? oAuth2AuthorizedClient.getRefreshToken().getTokenValue() : null
        );

        return res;
    }

    @Data
    class Token{
        private String idToken;
        private String accessToken;
        private String refreshToken;

        public Token(String tokenValue, String tokenValue1, String tokenValue2) {
            this.idToken = tokenValue;
            this.accessToken = tokenValue1;
            this.refreshToken = tokenValue2;
        }
    }
}
