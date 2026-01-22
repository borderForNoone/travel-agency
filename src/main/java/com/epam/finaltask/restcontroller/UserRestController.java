package com.epam.finaltask.restcontroller;

import com.epam.finaltask.dto.RemoteResponse;
import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.exception.StatusCodes;
import com.epam.finaltask.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserRestController {
    private final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<RemoteResponse> addUser(@RequestBody @Valid UserDTO userDTO) {
        UserDTO createdUserDto = userService.register(userDTO);
        RemoteResponse remoteResponse = RemoteResponse.create(
                true, StatusCodes.OK.name(), "User is successfully registered",
                List.of(createdUserDto)
        );
        return new ResponseEntity<>(remoteResponse, HttpStatus.CREATED);
    }
}
