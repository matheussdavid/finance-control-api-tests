package com.financecontrol.fixtures.authentication;

import com.financecontrol.models.authentication.LoginRequest;

public class LoginFixture {

    public static LoginRequest loginValidoComUsername() {
        return new LoginRequest("admin", "12345678");
    }

    public static LoginRequest loginValidoComEmail() {
        return new LoginRequest("admin@qa.com", "12345678");
    }
}
