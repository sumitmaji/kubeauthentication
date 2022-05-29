# Kubernetes Authentication Service

This provides api for authentication via Ldap, OAuth2.

# Installation Steps
```console
cd /root/kubeauthentication
./run_kubeauth.sh
```

1. It uses helm to deploy the service. The charts are present in `chart` directory. 
2. [`Ldap`](https://github.com/sumitmaji/kubernetes/tree/master/install_k8s/ldap) application should be running if you want to authenticate
user using ldap.
3. In order to use Auth0, we need below information
   1. Client ID
   2. Client Secret
   3. Realm
   4. The [`Link`](https://manage.auth0.com/dashboard/us/skmaji/applications/C3UHISO3z60iF1JLG8L7VPUSWOASrJfO/quickstart) contains
   how to set up application and use oauth. For setting up Auth0 also look into `Edward Viaene` tutorial on `Learn DevOps: Advanced Kubernetes Usage`.
   In this tutorial go to `authentication` and `authorization` video.
   6. [`application.yml`](https://github.com/sumitmaji/kubeauthentication/blob/main/src/main/resources/application.yml) should contains
   oauth2 details for authentication to work.

      
# Information about the service
1. it provides `/kubeauth` rest api for authentication via [`cli`](https://github.com/sumitmaji/kubernetes/tree/master/install_k8s/kube-login).
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

  2. [https://www.bezkoder.com/spring-boot-jwt-authentication/](https://www.bezkoder.com/spring-boot-jwt-authentication/)
- Udemy tutorials
   1. `Spring Boot Security and oAuth2 in depth from scratch`
   2. `OAuth 2-0 in Spring Boot Applications`
   3. `Spring Security Zero to Master along with JWT,OAUTH2`


