package com.spring_rest_api.api_paths.entity;

import com.google.firebase.internal.NonNull;

public class UserEntity {

    @NonNull
    public String name;
    public boolean admin;
    @NonNull
    public String secret;

    public UserEntity() {
        this.name = "";
        this.admin = false;
        this.secret = "";
    }

    public UserEntity(String name, boolean admin, String secret) {
        this.name = name;
        this.admin = admin;
        this.secret = secret;
    }

    public UserEntity(User user, String secret) {
        this.name = user.getName();
        this.admin = user.getIsAdmin();
        this.secret = secret;
    }
}
