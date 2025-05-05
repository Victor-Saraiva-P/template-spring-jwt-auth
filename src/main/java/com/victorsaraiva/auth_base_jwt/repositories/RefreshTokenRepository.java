package com.victorsaraiva.auth_base_jwt.repositories;

import com.victorsaraiva.auth_base_jwt.models.RefreshTokenEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
  Optional<RefreshTokenEntity> findByToken(String token);
}
