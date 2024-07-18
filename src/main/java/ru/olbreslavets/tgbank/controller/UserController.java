package ru.olbreslavets.tgbank.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.olbreslavets.tgbank.dto.UserDto;
import ru.olbreslavets.tgbank.service.UserService;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public void createUser(@RequestBody UserDto dto) {
        userService.createUser(dto);
    }

    @GetMapping
    public List<UserDto> getUsers() {
        return userService.getAllUsers();
    }

}
