package com.victorsaraiva.auth_base_jwt.mappers.impl.user;

import com.victorsaraiva.auth_base_jwt.dtos.user.UserResponseDTO;
import com.victorsaraiva.auth_base_jwt.mappers.Mapper;
import com.victorsaraiva.auth_base_jwt.models.UserEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserResponseMapperImpl implements Mapper<UserEntity, UserResponseDTO> {

  private final ModelMapper modelMapper;

  @Override
  public UserResponseDTO mapTo(UserEntity userEntity) {
    return modelMapper.map(userEntity, UserResponseDTO.class);
  }

  @Override
  public UserEntity mapFrom(UserResponseDTO userResponseDTO) {
    return modelMapper.map(userResponseDTO, UserEntity.class);
  }
}
