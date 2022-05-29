package com.sum.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IngressLdapSuccessHandler implements AuthenticationSuccessHandler {

    private String decode(String url) throws UnsupportedEncodingException {
        return URLDecoder.decode(url, StandardCharsets.UTF_8.toString());
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        String referer =  httpServletRequest.getHeader("Referer");
        Pattern compile = Pattern.compile("(.*)(rd=)(.*)");
        Matcher matcher = compile.matcher(referer);
        String redirectUrl = null;
        if(matcher.matches()){
            redirectUrl = decode(matcher.group(3));
        }else{
            httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Error: id tag");
            return;
        }
        httpServletResponse.sendRedirect(redirectUrl);

    }
}
