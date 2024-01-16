package com.authorisation.givens;

import com.authorisation.dto.UserDto;

public class UserDtoGivens {

    public static UserDto generateUserDto() {
        return UserDto.builder().id(1L).firstName("firstName")
                .lastName("lastName")
                .email("email")
                .token("token").build();
    }

}
