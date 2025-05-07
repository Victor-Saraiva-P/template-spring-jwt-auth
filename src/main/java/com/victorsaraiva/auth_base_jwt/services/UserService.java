package com.victorsaraiva.auth_base_jwt.services;

import com.victorsaraiva.auth_base_jwt.dtos.user.ChangeRoleRequestDTO;
import com.victorsaraiva.auth_base_jwt.dtos.user.UserDTO;
import com.victorsaraiva.auth_base_jwt.exceptions.user.UserNotFoundException;
import com.victorsaraiva.auth_base_jwt.mappers.Mapper;
import com.victorsaraiva.auth_base_jwt.models.UserEntity;
import com.victorsaraiva.auth_base_jwt.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final Mapper<UserEntity, UserDTO> userMapper;

  public List<UserDTO> getAllUsers() {
    List<UserEntity> userEntities = userRepository.findAll();

    return userEntities.stream().map(userMapper::mapTo).toList();
  }

  public UserDTO changeUserRole(ChangeRoleRequestDTO changeRoleRequestDTO, UUID userId) {
    UserEntity user =
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

    user.setRole(changeRoleRequestDTO.getRoleEnum());
    userRepository.save(user);
    return userMapper.mapTo(user);
  }
}
