package com.sum.security;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

@RestController
public class KubeController {

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

    /**
     * This api validates user credentials with Auth0 openid connect.
     * If the authentication is successful, then it returns id_token and access_token.
     * If the the authentication is unsuccessful, then it returns error.
     * This api is used by the kube-login cli
     * (https://github.com/sumitmaji/kubernetes/tree/master/install_k8s/kube-login) to validate
     * user's credentials. On successful login, the cli use the id_token and sends the same
     * to kubernetes for validation. Upon validation, the kubernetes allows/rejects the operation.
     * @param username
     * @param password
     * @return
     */
    @GetMapping("/kubectl")
    public String getToken(@RequestParam String username, @RequestParam String password) {
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

    /**
     * Ldap authentication manager, used for validating user credentials
     * in ldap.
     */
    @Autowired
    private AuthenticationManager authManager;


    /**
     * The api validates the user credentials against the ldap server
     * and returns the TokenReview object. If the user credentials are valid
     * then the TokenReview object is send with authenticated=true
     * and groups=['developers', 'administrator'] attributes are set.
     * If the user credentials are not valid then TokenReview Object is send
     * with authenticated=false attribute are set.
     *
     * Before this api, the request is intercepted by KubernetesAuthFilter with sets
     * the username and password attribute before being forwarded to this api. For more
     * details about what this Filter does, please check the code documentation.
     *
     * This feature is developed for kubernetes authentication via proxy, where kubernetes would
     * send TokenReview object to proxy api (/kubeauth) present in the configuration
     * and expects TokenReview response.
     * @param request
     * @return
     */
    @PostMapping("/kubeauth")
    public TokenReview getTokenReview(HttpServletRequest request) {
        Authentication authenticate = null;
        try {
            authenticate = authManager.authenticate(new UsernamePasswordAuthenticationToken(
                    request.getAttribute("username"), request.getAttribute("password")
            ));
        } catch (InternalAuthenticationServiceException e) {
            System.out.println(e);
        } catch (Exception e) {
            System.out.println(e);
        }

        TokenReview tokenReview = null;
        if (authenticate == null || authenticate instanceof AnonymousAuthenticationToken) {
            tokenReview = new TokenReview(false, null);
        } else {
            tokenReview = new TokenReview(true, authenticate.getName());
        }
        return tokenReview;
    }

    @Data
    class TokenReview {
        private String apiVersion = "authentication.k8s.io/v1";
        private String kind = "TokenReview";
        private Status status;

        public TokenReview(boolean isAuthenticated, String userName) {
            Status status = new Status(userName);
            status.setAuthenticated(Boolean.toString(isAuthenticated));

            this.status = status;
        }

        @Data
        class Status {
            private User user;
            private String authenticated;

            public Status(String userName) {
                if(userName != null){
                    User user = new User();
                    user.setUsername(userName);
                    user.setGroups(Arrays.asList("developers", "administrator"));
                    this.user = user;
                }
            }

            @Data
            class User {
                private String username;
                private String uid;
                private List<String> groups;
            }
        }

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
