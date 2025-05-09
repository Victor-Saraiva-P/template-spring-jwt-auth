package com.victorsaraiva.auth_base_jwt.services;

import com.victorsaraiva.auth_base_jwt.dtos.user.ChangeRoleRequestDTO;
import com.victorsaraiva.auth_base_jwt.dtos.user.UserDTO;
import com.victorsaraiva.auth_base_jwt.exceptions.user.UserNotFoundException;
import com.victorsaraiva.auth_base_jwt.mappers.Mapper;
import com.victorsaraiva.auth_base_jwt.models.UserEntity;
import com.victorsaraiva.auth_base_jwt.repositories.RefreshTokenRepository;
import com.victorsaraiva.auth_base_jwt.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final RefreshTokenRepository refreshTokenRepository;
  private final Mapper<UserEntity, UserDTO> userMapper;

  public List<UserDTO> getAllUsers() {
    List<UserEntity> userEntities = userRepository.findAll();

    return userEntities.stream().map(userMapper::mapTo).toList();
  }

  @Transactional
  public void changeUserRole(ChangeRoleRequestDTO changeRoleRequestDTO, UUID userId) {

    log.info("Tentando mudar o papel do usuário com o id {}", userId);
    UserEntity user =
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

    user.setRole(changeRoleRequestDTO.getRoleEnum());
    userRepository.save(user);
    userMapper.mapTo(user);
  }

  @Transactional
  public void deleteUser(UUID userId) {

    log.info("Tentando deletar o usuário com o id {}", userId);
    UserEntity user =
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

    // Remove todos os refresh tokens do usuario
    refreshTokenRepository.deleteAllByUserId(userId);

    // Remove o usuario
    userRepository.delete(user);
  }
}
