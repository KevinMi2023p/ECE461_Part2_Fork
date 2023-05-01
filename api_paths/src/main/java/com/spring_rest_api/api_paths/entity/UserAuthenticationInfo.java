package com.spring_rest_api.api_paths.entity;


public class UserAuthenticationInfo {
    private Secret secret;

    public Secret getSecret() {
        return secret;
    }

    public void setSecret(Secret secret) {
        this.secret = secret;
    }
}
