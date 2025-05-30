package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.UserDto;
import org.example.exceptions.IllegalRoleException;
import org.example.exceptions.UserNotFoundException;
import org.example.storage.UserRepository;
import org.example.util.Roles;
import org.example.util.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userStorage;


    public UserDto getUser(Long userId) {
        return userStorage.findById(userId).map(UserMapper::userToUserDto)
                .orElseThrow(() -> new UserNotFoundException("Нет пользователя с id = " + userId));
    }

    @Transactional
    public UserDto updateRole(Long userId, String role) {
        try {
            Roles.valueOf(role);
        } catch (IllegalArgumentException e) {
            throw new IllegalRoleException(String.format("Роль %s - невалидна", role));
        }
        return userStorage.findById(userId).map(user -> {
            user.setRole(role);
            userStorage.saveAndFlush(user);
            return UserDto.builder().role(user.getRole()).username(user.getUsername()).build();
        }).orElseThrow(() -> new UserNotFoundException("Нет пользователя с id = " + userId));
    }

    public List<UserDto> getAll() {
        return userStorage.findAll().stream().map(UserMapper::userToUserDto).toList();
    }
}
