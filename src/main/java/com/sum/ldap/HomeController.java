package com.sum.ldap;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
