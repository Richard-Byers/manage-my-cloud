package com.authorisation.mappers;

import com.authorisation.dto.UserDto;
import com.authorisation.entities.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toUserDto(UserEntity user);

}