package com.financecontrol.fixtures.authentication;

import com.financecontrol.models.authentication.LoginRequest;

public final class LoginFixture {

    private static final String SENHA_PADRAO = "12345678";

    private LoginFixture() {
    }

    public static LoginRequest loginValidoComUsername() {
        return new LoginRequest("admin", SENHA_PADRAO);
    }

    public static LoginRequest loginValidoComEmail() {
        return new LoginRequest("admin@qa.com", SENHA_PADRAO);
    }

    public static LoginRequest loginComIdentificadorNulo() {
        return new LoginRequest(null, SENHA_PADRAO);
    }

    public static LoginRequest loginComSenhaNula() {
        return new LoginRequest("admin", null);
    }

    public static LoginRequest loginComCredenciaisNulas() {
        return new LoginRequest(null, null);
    }
}