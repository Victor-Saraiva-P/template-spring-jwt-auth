package com.victorsaraiva.auth_base_jwt.repositories;

import com.victorsaraiva.auth_base_jwt.models.UserEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface UserRepository extends JpaRepository<UserEntity, UUID> {}
