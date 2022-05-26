package com.sum.security;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

@RestController
public class HomeController {

    @GetMapping("/welcome")
    public String index() {
        return "Welcome to the home page!";
    }

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

    @GetMapping(value = "/302")
    public void redirect(HttpServletResponse response) throws IOException {
        response.sendRedirect("/echo");
    }

    @GetMapping(value = "/")
    public void home(HttpServletResponse response) throws IOException {
        response.sendRedirect("/home.html");
    }

}
