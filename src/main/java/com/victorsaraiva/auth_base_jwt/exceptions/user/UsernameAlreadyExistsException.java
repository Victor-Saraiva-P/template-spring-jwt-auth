package com.victorsaraiva.auth_base_jwt.exceptions.user;

public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException(String username) {
        super("Username " + username + " já cadastrado");
    }
}
