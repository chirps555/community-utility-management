package com.example.sdfguanlixt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginResponse {

    private String token;

    @JsonProperty("residentId")
    private String residentId;

    private String name;
    private String role;

    public LoginResponse() {
    }

    public LoginResponse(String token, String residentId, String name, String role) {
        this.token = token;
        this.residentId = residentId;
        this.name = name;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getResidentId() {
        return residentId;
    }

    public void setResidentId(String residentId) {
        this.residentId = residentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
