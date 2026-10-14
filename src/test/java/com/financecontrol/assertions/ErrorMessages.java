package com.financecontrol.assertions;

public final class ErrorMessages {

    private ErrorMessages() {}

    public static final String FALHA_VALIDACAO = "Falha na validação";
    public static final String CREDENCIAIS_INVALIDAS = "Usuário ou senha inválidos";

    public static final String NOME_OBRIGATORIO = "O nome é obrigatório";
    public static final String USUARIO_OBRIGATORIO = "O usuário é obrigatório";
    public static final String IDENTIFICADOR_OBRIGATORIO = "Usuário ou e-mail é obrigatório";
    public static final String EMAIL_OBRIGATORIO = "O e-mail é obrigatório";
    public static final String SENHA_OBRIGATORIA = "A senha é obrigatória";
    public static final String CONFIRMACAO_SENHA_OBRIGATORIA = "A confirmação de senha é obrigatória";

    public static final String EMAIL_INVALIDO = "O e-mail deve ser válido";
    public static final String EMAIL_MAX_50 = "O e-mail deve ter no máximo 50 caracteres";
    public static final String NOME_TAMANHO = "O nome deve ter entre 3 e 50 caracteres";
    public static final String USUARIO_TAMANHO = "O usuário deve ter entre 3 e 50 caracteres";
    public static final String SENHA_TAMANHO = "A senha deve ter entre 8 e 30 caracteres";

    public static final String USUARIO_JA_CADASTRADO = "Usuário já cadastrado";
    public static final String EMAIL_JA_CADASTRADO = "E-mail já cadastrado";
    public static final String SENHAS_NAO_COINCIDEM = "As senhas não coincidem";
}
