package com.spring_rest_api.api_paths.entity;

import com.google.cloud.firestore.annotation.PropertyName;

public class PagQuery {
    public String Version;
    public String Name;

    @PropertyName("Version")
    public void set_Version(String version){
        this.Version = version;
    }

    public String get_Version() {
        return this.Version;
    }

    @PropertyName("Name")
    public void set_Name(String name){
        this.Name = name;
    }

    public String get_Name() {
        return this.Name;
    }
}
