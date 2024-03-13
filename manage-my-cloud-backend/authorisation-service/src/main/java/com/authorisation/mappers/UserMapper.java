package com.authorisation.mappers;

import com.authorisation.dto.UserDto;
import com.authorisation.entities.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "profileImage", target = "profileImage") // workaround for byte[] mapping issue involving postgres and hibernate bytea type.
    @Mapping(source = "firstLogin", target = "firstLogin")
    UserDto toUserDto(UserEntity user);

}