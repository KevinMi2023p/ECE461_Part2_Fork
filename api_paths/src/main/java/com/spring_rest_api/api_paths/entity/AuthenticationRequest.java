package com.spring_rest_api.api_paths.entity;

public class AuthenticationRequest {

    private User user;
    private Secret secret;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Secret getSecret() {
        return secret;
    }

    public void setSecret(Secret secret) {
        this.secret = secret;
    }
}
