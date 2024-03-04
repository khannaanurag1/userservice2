package org.example.userservice2.Controllers;

import org.antlr.v4.runtime.misc.Pair;
import org.example.userservice2.Dtos.*;
import org.example.userservice2.Models.SessionStatus;
import org.example.userservice2.Models.User;
import org.example.userservice2.Services.AuthService;
import org.example.userservice2.Services.AuthService2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService2 authService;

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        try {
            Pair<User, MultiValueMap<String,String>> userWithHeaders = authService.login(loginRequestDto.getEmail(), loginRequestDto.getPassword());
            UserDto userDto = getUserDto(userWithHeaders.a);
            return new ResponseEntity<>(userDto, userWithHeaders.b, HttpStatus.OK);
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

    @PostMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestBody ValidateTokenRequestDto validateTokenRequestDto) {
       Boolean validated = authService.validateToken(validateTokenRequestDto.getToken(),validateTokenRequestDto.getId());
        return new ResponseEntity<>(validated,HttpStatus.OK);
    }

    private UserDto getUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setEmail(user.getEmail());
        userDto.setRoles(new HashSet<>());
        return userDto;
    }
}
