package org.example.userservice2.Controllers;

import org.example.userservice2.Dtos.LoginRequestDto;
import org.example.userservice2.Dtos.LogoutRequestDto;
import org.example.userservice2.Dtos.SignUpRequestDto;
import org.example.userservice2.Dtos.UserDto;
import org.example.userservice2.Models.User;
import org.example.userservice2.Services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        try {
            //User user = authService.login(loginRequestDto.getEmail(), loginRequestDto.getPassword());
            //UserDto userDto = getUserDto(user);
            //return new ResponseEntity<>(userDto, HttpStatus.OK);
            return authService.login(loginRequestDto.getEmail(), loginRequestDto.getPassword());
        } catch(Exception ex) {
            return new ResponseEntity<>(null,HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signUp(@RequestBody SignUpRequestDto signUpRequestDto) {
        User user = authService.signUp(signUpRequestDto.getEmail(),signUpRequestDto.getPassword());
        UserDto userDto = getUserDto(user);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LogoutRequestDto logoutRequestDto) {
        return null;
    }

    private UserDto getUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setEmail(user.getEmail());
        userDto.setRoles(new HashSet<>());
        return userDto;
    }
}
