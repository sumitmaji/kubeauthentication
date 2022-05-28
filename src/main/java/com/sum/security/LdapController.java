package com.sum.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class LdapController {

    @GetMapping("/healthz")
    public String healthz(){
        return "OK";
    }

    @GetMapping(value = "/echo")
    public void echo(HttpServletRequest request, HttpServletResponse response) {
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                response.setHeader(String.format("x-echo-%s", headerName.toLowerCase()), request.getHeader(headerName));
            }
        }
    }

    @GetMapping(value="/authorize")
    public ResponseEntity<String> authorize(Authentication authenticationTemp){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication instanceof AnonymousAuthenticationToken){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: Unauthorized");
        }else if(authentication instanceof UsernamePasswordAuthenticationToken && authentication.isAuthenticated()){
            return ResponseEntity.status(HttpStatus.OK).body("OK authenticated");
        }else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: Unauthorized");
        }
    }

    @Autowired
    private AuthenticationManager authManager;
    @PostMapping(value = "login_user")
    public void loginUser(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String referer =  request.getHeader("Referer");
        Pattern compile = Pattern.compile("(.*)(id=)(.*)");
        Matcher matcher = compile.matcher(referer);
        String redirectUrl = null;
        if(matcher.matches()){
            redirectUrl = matcher.group(3);
        }else{
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Error: id tag");
            return;
        }

        Authentication authenticate = null;
        try {
            authenticate = authManager.authenticate(new UsernamePasswordAuthenticationToken(
                    request.getParameter("username"), request.getParameter("password")

            ));
            SecurityContextHolder.getContext().setAuthentication(authenticate);
        } catch (InternalAuthenticationServiceException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
            return;
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
            return;
        }

        if (authenticate == null || authenticate instanceof AnonymousAuthenticationToken) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized");
        } else {
            response.sendRedirect(redirectUrl);
        }

    }

}
