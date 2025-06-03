package org.example.util;

import org.example.dto.UserDto;
import org.example.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public static UserDto userToUserDto(User user){
        return UserDto
                .builder()
                .role(user.getRole())
                .username(user.getUsername())
                .build();
    }
}
