package com.victorsaraiva.auth_base_jwt.repositories;

import com.victorsaraiva.auth_base_jwt.models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);

    Optional<Object> findByUsername(String username);
}
