package com.victorsaraiva.auth_base_jwt.exceptions.user;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
        super("Email " + email + " já cadastrado");
    }
}
