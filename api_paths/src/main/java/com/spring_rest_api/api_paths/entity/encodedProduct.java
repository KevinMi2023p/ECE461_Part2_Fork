package com.spring_rest_api.api_paths.entity;

import com.google.cloud.firestore.annotation.PropertyName;

public class encodedProduct {
    public String Content;
    public String URL;
    public String JSProgram;
    @PropertyName("Content")
    public String getContent() {
        return Content;
    }
    @PropertyName("Content")
    public void setContent(String content) {
        Content = content;
    }
    @PropertyName("URL")
    public String getURL() {
        return URL;
    }

    @PropertyName("URL")
    public void setURL(String URL) {
        this.URL = URL;
    }
    @PropertyName("JSProgram")
    public String getJSProgram() {
        return JSProgram;
    }
    @PropertyName("JSProgram")
    public void setJSProgram(String JSProgram) {
        this.JSProgram = JSProgram;
    }
}
