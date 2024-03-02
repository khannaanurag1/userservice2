package org.example.userservice2.Services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import org.antlr.v4.runtime.misc.MultiMap;
import org.example.userservice2.Dtos.UserDto;
import org.example.userservice2.Models.User;
import org.example.userservice2.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public User signUp(String email, String password) {
        User user = new User();
        user.setEmail(email);
        //user.setPassword(password);
        user.setPassword(bCryptPasswordEncoder.encode(password));

        User savedUser = userRepository.save(user);
        return savedUser;
    }

    public ResponseEntity<UserDto> login(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if(userOptional.isEmpty()) {
            return null;
        }

        User user = userOptional.get();

        if(!bCryptPasswordEncoder.matches(password,user.getPassword())) {
            return null;
        }

        String message = "{\n" +
        "   \"email\": \"anurag@scaler.com\",\n" +
        "   \"roles\": [\n" +
        "      \"instructor\",\n" +
        "      \"buddy\"\n" +
        "   ],\n" +
        "   \"expirationDate\": \"2ndApril2024\"\n" +
        "}";

        byte[] content = message.getBytes(StandardCharsets.UTF_8);

        //BELOW TOKEN WILL NOT HAVE ANY SIGNATURE, IN CASE OF SIGNATURE GENERATION, USE BELOW CODE
        //String token = Jwts.builder().content(content).compact();

        MacAlgorithm algorithm = Jwts.SIG.HS256;
        SecretKey secretKey = algorithm.key().build();
        //String token = Jwts.builder().content(content).signWith(secretKey,algorithm).compact();


        Map<String,Object> jwtData = new HashMap<>();
        jwtData.put("email",user.getEmail());
        jwtData.put("roles",user.getRoles());
        jwtData.put("expiryTime",new Date());
        jwtData.put("createdAt",new Date());
        String token = Jwts.builder().claims(jwtData).signWith(secretKey,algorithm).compact();

        MultiValueMap<String,String> headers = new LinkedMultiValueMap<>();
        headers.add(HttpHeaders.SET_COOKIE,"auth-token "+token);
        UserDto userDto = getUserDto(user);
        return new ResponseEntity<>(userDto,headers, HttpStatus.OK);
    }

    private UserDto getUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setEmail(user.getEmail());
        userDto.setRoles(new HashSet<>());
        return userDto;
    }
}
