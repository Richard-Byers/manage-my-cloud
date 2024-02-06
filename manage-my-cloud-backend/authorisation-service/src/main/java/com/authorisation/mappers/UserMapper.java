package com.authorisation.mappers;

import com.authorisation.dto.UserDto;
import com.authorisation.entities.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "profileImage", target = "profileImage")
    UserDto toUserDto(UserEntity user);

}