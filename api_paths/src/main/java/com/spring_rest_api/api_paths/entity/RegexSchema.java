package com.spring_rest_api.api_paths.entity;

import com.google.cloud.firestore.annotation.PropertyName;

public class RegexSchema {
    
    public String RegEx;

    @PropertyName("RegEx")
    public void setRegex(String _reg) {
        // this.regex = _reg;
        RegEx = _reg;
    }

    @PropertyName("RegEx")
    public String getRegex() {
        // return this.regex;
        return RegEx;
    }
}
