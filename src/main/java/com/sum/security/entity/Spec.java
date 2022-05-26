package com.sum.security.entity;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class Spec {
    private String token;

    Spec() {
    }

    public String getToken(){
        return this.token;
    }
}
