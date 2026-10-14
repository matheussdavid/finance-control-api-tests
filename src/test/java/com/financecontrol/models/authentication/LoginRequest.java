package com.financecontrol.models.authentication;

public class LoginRequest {

    private final String identifier;
    private final String password;

    public LoginRequest(String identifier, String password) {
        this.identifier = identifier;
        this.password = password;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getPassword() {
        return password;
    }
}