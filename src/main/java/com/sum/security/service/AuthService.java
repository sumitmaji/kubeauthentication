package com.sum.security.service;

public interface AuthService {
    String fetchToken(String username, String password);
}
