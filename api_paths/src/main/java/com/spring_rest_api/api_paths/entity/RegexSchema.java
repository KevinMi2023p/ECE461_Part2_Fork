package com.spring_rest_api.api_paths.entity;

import com.google.cloud.firestore.annotation.PropertyName;

public class RegexSchema {
    
    private String regex;

    @PropertyName("RegEx")
    public void setRegex(String _reg) {
        this.regex = _reg;
    }

    public String getRegex() {
        return this.regex;
    }
}
