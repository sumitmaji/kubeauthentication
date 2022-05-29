package com.sum.security.filters;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sum.security.entity.Spec;
import com.sum.security.entity.TokenReview;
import lombok.Data;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StreamUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class KubernetesAuthFilter extends OncePerRequestFilter {

    public KubernetesAuthFilter() {
    }

    /**
     * This filter intercepts the requests and validates if the request body contains
     * TokenReview object or not. If the request body does not contain the TokenReview object
     * then it does nothing forward the request to next filter. If the request body contains
     * TokenReview object then it extracts the username and password from the token attribute
     * of the object and attach them against username and password attribute in request object
     * before being sent to next filter.
     *
     * This feature is developed for kubernetes authentication via proxy, where kubernetes would
     * send TokenReview object to proxy url configured and expects TokenReview response.
     * @param httpServletRequest
     * @param httpServletResponse
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {


        ServletInputStream inputStream = httpServletRequest.getInputStream();
        if(inputStream != null){
            String s = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            if(s != null && !s.isEmpty()){
                try{
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

                    TokenReview tokenReview = objectMapper.readValue(s, TokenReview.class);
                    String[] data = tokenReview.getSpec().getToken().split(":");

                    httpServletRequest.setAttribute("username", data[0]);
                    httpServletRequest.setAttribute("password", data[1]);

                }catch (Exception e){
                    System.out.println("e = " + e);
                }
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
