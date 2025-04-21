package com.victorsaraiva.auth_base_jwt.exceptions.user;

public class EmailNotFoundException extends RuntimeException {
    public EmailNotFoundException(String email) {
        super("Email " + email + " não encontrado");
    }
}
