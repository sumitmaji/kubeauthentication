# Kubernetes Authentication Service

This provides api for authentication via Ldap, OAuth2.

# Installation Steps

```console
cd /root/kubeauthentication
./run_kubeauth.sh
```

1. It uses helm to deploy the service. The charts are present in `chart` directory.
2. [`Ldap`](https://github.com/sumitmaji/kubernetes/tree/master/install_k8s/ldap) application should be running if you
   want to authenticate
   user using ldap.
3. In order to use Auth0, we need below information
    1. Client ID
    2. Client Secret
    3. Realm
    4.
    The [`Link`](https://manage.auth0.com/dashboard/us/skmaji/applications/C3UHISO3z60iF1JLG8L7VPUSWOASrJfO/quickstart)
    contains
    how to set up application and use oauth. For setting up Auth0 also look into `Edward Viaene` tutorial
    on `Learn DevOps: Advanced Kubernetes Usage`.
    In this tutorial go to `authentication` and `authorization` video.
    6. [`application.yml`](https://github.com/sumitmaji/kubeauthentication/blob/main/src/main/resources/application.yml)
       should contains
       oauth2 details for authentication to work.

# Information about the service

1. it provides `/kubeauth` rest api for authentication
   via [`cli`](https://github.com/sumitmaji/kubernetes/tree/master/install_k8s/kube-login).
   The api returns the id_token which is used by cli to provide to kubernetes service. it uses Auth0 OpenId
   connect for authentication.

```console
alias kctl='kubectl --kubeconfig=/root/oauth.conf --token=$(python3 /root/kubernetes/install_k8s/kube-login/cli-auth.py)' 
```

2. It provides `/check` and `/authenticate` api which is used by ingress controller to authenticate user
   when they access protected url. It uses ldap for authentication. `/check` rest api send 401 (UnAuthorized)
   if the user is not authenticated, and 200 (OK) is the user is authenticated. `/authenticate` rest api
   presents login page to the user. Upon successful authentication, it redirects user to the target page which
   was sent by the ingress controller to the login service a request parameter.

#### Tags which should be put in Ingress resource to enable authentication via this service is:

`nginx.ingress.kubernetes.io/auth-signin: https://master.cloud.com:32028/authenticate`
`nginx.ingress.kubernetes.io/auth-url: https://master.cloud.com:32028/check`

3. It provides `/oauth2/token` api for authenticating user using Auth0 OpenId Connect. It provides id_token
   and access_token as response to user.

# Documents and Useful Link

- Below links provide good architecture and code examples of customizing spring security
    1. [https://www.bezkoder.com/spring-boot-jwt-mysql-spring-security-architecture](https://www.bezkoder.com/spring-boot-jwt-mysql-spring-security-architecture)
    It Implements below architecture
    ![alt text](https://github.com/sumitmaji/kubeauthentication/blob/main/images/img.png)

    3. [https://www.bezkoder.com/spring-boot-jwt-authentication/](https://www.bezkoder.com/spring-boot-jwt-authentication/)
- Udemy tutorials
    1. `Spring Boot Security and oAuth2 in depth from scratch`
    2. `OAuth 2-0 in Spring Boot Applications`
    3. `Spring Security Zero to Master along with JWT,OAUTH2`
    4. `Learn DevOps Advanced Kubernetes Usage` (Authentication and Authorization)

- Good Slides to learn about Oauth

### OAuth 2 Grant Types
1. [`Authorization Code Grant Type`](https://docs.google.com/presentation/d/1CiAiuay5rd1KDDnYwOyu6ud9xk5ZetSQDOMp9DYUKjs/edit?usp=sharing)
2. [`Client Credentials`](https://docs.google.com/presentation/d/1KEA3i0F0bhB4me1uHfXkbmuaaFeRyxo7rG0ih-MlP68/edit?usp=sharing)
3. [`Password`](https://docs.google.com/presentation/d/1kea9VCSP_QtQSb_NbU7MPOVLEF20iuOgieNW1g1MTwc/edit?usp=sharing)
4. [`Device flow`](https://docs.google.com/presentation/d/1SlGr9z9bFIxYOLzwwZco3ny2W1XZ-9GJ9ORWA73gbjo/edit?usp=sharing)
5. [`PKCE-Enhanced Authorization code`](https://docs.google.com/presentation/d/1yJeYPMoPY2050cZkkBcBu1SL5Z88StV7O7fH5f_6X3A/edit?usp=sharing)
6. [`Refreshing Access Token`](https://docs.google.com/presentation/d/1e0bWzKk5JxxGXlAvAyeWe1q8iYgY804Y6vZ-zCpRAKU/edit?usp=sharing)

### Information about Cors and Csrf [`Link`](https://docs.google.com/presentation/d/1to1mP1R01DNK80GrWP6b64I_l0PJ6pMxECLyTo2Uf9o/edit?usp=sharing)
