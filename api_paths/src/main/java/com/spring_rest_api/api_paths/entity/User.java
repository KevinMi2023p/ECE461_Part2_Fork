package com.spring_rest_api.api_paths.entity;

import java.util.*;

import com.fasterxml.jackson.annotation.JsonProperty;

public class User {
    
    @JsonProperty("name")
    private String name;
    private boolean isAdmin;
    private Map<String, String> userAuthenticationInfo;

    public User() {}

    public Map<String, String> getUserAuthenticationInfo() {
        return userAuthenticationInfo;
    }

    public void setUserAuthenticationInfo(Map<String, String> userAuthenticationInfo) {
        this.userAuthenticationInfo = userAuthenticationInfo;
    }

    public User(String name, boolean isAdmin) {
        this.name = name;
        this.isAdmin = isAdmin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean admin) {
        this.isAdmin = admin;
    }


}
