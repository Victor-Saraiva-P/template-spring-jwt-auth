package com.victorsaraiva.auth_base_jwt.exceptions.user;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String uuid) {
        super("Usuário com UUID " + uuid + " não encontrada");
    }
}
