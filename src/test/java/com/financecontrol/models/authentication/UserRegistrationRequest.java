package com.financecontrol.models.authentication;

public class UserRegistrationRequest {

    private String name;
    private String username;
    private String email;
    private String password;
    private String confirmPassword;

    public UserRegistrationRequest(String name, String username, String email, String password, String confirmPassword) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

    public String getName() { return name; }

    public String getUsername() { return username; }

    public String getEmail() { return email; }

    public String getPassword() { return password; }

    public String getConfirmPassword() { return confirmPassword; }
}
