package com.victorsaraiva.auth_base_jwt.mappers;

public interface Mapper<A, B> {

    B mapTo(A a);

    A mapFrom(B b);

}