package com.victorsaraiva.auth_base_jwt.mappers.impl.user;

import com.victorsaraiva.auth_base_jwt.dtos.auth.SignupRequestDTO;
import com.victorsaraiva.auth_base_jwt.mappers.Mapper;
import com.victorsaraiva.auth_base_jwt.models.UserEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SignupUserRequestMapperImpl implements Mapper<UserEntity, SignupRequestDTO> {

  private final ModelMapper modelMapper;

  @Override
  public SignupRequestDTO mapTo(UserEntity userEntity) {
    return modelMapper.map(userEntity, SignupRequestDTO.class);
  }

  @Override
  public UserEntity mapFrom(SignupRequestDTO userDTO) {
    return modelMapper.map(userDTO, UserEntity.class);
  }
}
