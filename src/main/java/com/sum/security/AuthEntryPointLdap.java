package com.sum.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * If the user tries to access secured resource and the user is not authenticated,
 * then spring boot forwards to request to AuthenticationEntryPoint. You configure this
 * entrypoint in exceptionHanler() method of http parameter in configure() method overriden
 * from WebSecurityConfigurerAdapter.
 *
 * here, is the user is not authenticated, then it sends 401 (UnAuthorized)
 */
@Configuration
public class AuthEntryPointLdap implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized");
    }
}
