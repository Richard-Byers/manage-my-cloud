package com.authorisation.mapper;

import com.authorisation.dto.UserDto;
import com.authorisation.entities.UserEntity;
import com.authorisation.mappers.UserMapper;
import com.authorisation.mappers.UserMapperImpl;
import org.junit.jupiter.api.Test;

import static com.authorisation.givens.UserEntityGivens.generateUserEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UserMapperTest {

    UserMapper userMapper = new UserMapperImpl();

    @Test
    void toUserDto() {

        //given
        UserEntity userEntity = generateUserEntity();

        //when
        UserDto userDto = userMapper.toUserDto(userEntity);

        //then
        assertEquals(userEntity.getId(), userDto.getId());
        assertEquals(userEntity.getFirstName(), userDto.getFirstName());
        assertEquals(userEntity.getLastName(), userDto.getLastName());
        assertEquals(userEntity.getEmail(), userDto.getEmail());
    }

}
