package com.financecontrol.models.authentication;

public class UserRegistrationRequest {

    private final String name;
    private final String username;
    private final String email;
    private final String password;
    private final String confirmPassword;

    public UserRegistrationRequest(String name, String username, String email, String password, String confirmPassword) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

    public UserRegistrationRequest withName(String name) {
        return new UserRegistrationRequest(name, username, email, password, confirmPassword);
    }

    public UserRegistrationRequest withUsername(String username) {
        return new UserRegistrationRequest(name, username, email, password, confirmPassword);
    }

    public UserRegistrationRequest withEmail(String email) {
        return new UserRegistrationRequest(name, username, email, password, confirmPassword);
    }

    public UserRegistrationRequest withPassword(String password) {
        return new UserRegistrationRequest(name, username, email, password, confirmPassword);
    }

    public UserRegistrationRequest withConfirmPassword(String confirmPassword) {
        return new UserRegistrationRequest(name, username, email, password, confirmPassword);
    }

    public String getName() { return name; }

    public String getUsername() { return username; }

    public String getEmail() { return email; }

    public String getPassword() { return password; }

    public String getConfirmPassword() { return confirmPassword; }
}
