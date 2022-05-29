package com.sum.security.entity;
import lombok.Data;

@Data
public class TokenReview {
    private String apiVersion;
    private String kind;
    private Spec spec;

    TokenReview() {
    }
}

