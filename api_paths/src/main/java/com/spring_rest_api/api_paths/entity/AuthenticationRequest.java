package com.spring_rest_api.api_paths.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthenticationRequest {

    @JsonProperty("User")
    private User User;
    @JsonProperty("Secret")
    private Secret Secret;

    public User getUser() {
        return User;
    }

    public void setUser(User user) {
        this.User = user;
    }

    public Secret getSecret() {
        return Secret;
    }

    public void setSecret(Secret secret) {
        this.Secret = secret;
    }
}
