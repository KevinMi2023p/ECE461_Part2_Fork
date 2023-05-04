package com.spring_rest_api.api_paths.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.firebase.internal.NonNull;

public class User {
    
    @JsonProperty("name")
    @NonNull
    public String name;
    
    public boolean isAdmin;

    public User() {
        this.name = "";
        this.isAdmin = false;
    }

    public User(@NonNull String name, boolean isAdmin) {
        this.name = name;
        this.setIsAdmin(isAdmin);
    }

    public User(UserEntity user) {
        this.name = user.name;
        this.isAdmin = user.admin;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }
}
