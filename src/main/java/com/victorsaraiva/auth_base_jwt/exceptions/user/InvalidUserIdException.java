package com.victorsaraiva.auth_base_jwt.exceptions.user;

public class InvalidUserIdException extends RuntimeException {
    public InvalidUserIdException() {
        super("O userId não pode ser nulo ou vazio");
    }
}
