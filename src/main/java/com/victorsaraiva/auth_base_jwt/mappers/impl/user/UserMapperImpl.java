package com.victorsaraiva.auth_base_jwt.mappers.impl.user;

import com.victorsaraiva.auth_base_jwt.dtos.user.UserDTO;
import com.victorsaraiva.auth_base_jwt.mappers.Mapper;
import com.victorsaraiva.auth_base_jwt.models.UserEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapperImpl implements Mapper<UserEntity, UserDTO> {

  private final ModelMapper modelMapper;

  @Override
  public UserDTO mapTo(UserEntity userEntity) {
    return modelMapper.map(userEntity, UserDTO.class);
  }

  @Override
  public UserEntity mapFrom(UserDTO userDTO) {
    return modelMapper.map(userDTO, UserEntity.class);
  }
}
