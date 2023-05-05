package com.spring_rest_api.api_paths.entity;

import com.google.firebase.database.PropertyName;

public class LinkedList {

    private String Next;

    private String Context;

    @PropertyName("Next")
    public String getNext() {
        return Next;
    }
    @PropertyName("Next")
    public void setNext(String next) {
        this.Next = next;
    }

    @PropertyName("Context")
    public String getContext() {
        return Context;
    }

    @PropertyName("Context")
    public void setContext(String context) {
        Context = context;
    }
}
