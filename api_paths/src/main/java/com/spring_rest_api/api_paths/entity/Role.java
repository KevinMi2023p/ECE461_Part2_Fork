package com.spring_rest_api.api_paths.entity;

public enum Role {
    User("USER"),
    Admin("ADMIN");

    public final String value;

    private Role(String value) {
        this.value = value;
    }
}
