package com.victorsaraiva.auth_base_jwt.services;

import com.victorsaraiva.auth_base_jwt.dtos.user.UserDTO;
import com.victorsaraiva.auth_base_jwt.mappers.Mapper;
import com.victorsaraiva.auth_base_jwt.models.UserEntity;
import com.victorsaraiva.auth_base_jwt.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final Mapper<UserEntity, UserDTO> userMapper;

  public List<UserDTO> getAllUsers() {
    List<UserEntity> userEntities = userRepository.findAll();

    return userEntities.stream().map(userMapper::mapTo).toList();
  }
}
