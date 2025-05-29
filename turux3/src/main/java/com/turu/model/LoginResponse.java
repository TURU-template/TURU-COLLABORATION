package com.turu.model;

import java.util.Map;

public class LoginResponse {
    private Integer id;
    private String username;

    public LoginResponse() {}

    public LoginResponse(Integer id, String username) {
        this.id = id;
        this.username = username;
    }

    // Getters & Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
