package com.spring_rest_api.api_paths.entity;

import com.google.cloud.firestore.annotation.PropertyName;

public class PagQuery {
    public String Version;
    public String Name;

    @PropertyName("Version")
    void set_Version(String version){
        this.Version = version;
    }

    String get_Version() {
        return this.Version;
    }

    @PropertyName("Name")
    void set_Name(String name){
        this.Name = name;
    }

    String get_String() {
        return this.Name;
    }
}
