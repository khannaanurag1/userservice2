package org.example.userservice2.Services;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.MacAlgorithm;
import org.antlr.v4.runtime.misc.Pair;
import org.example.userservice2.Models.Session;
import org.example.userservice2.Models.SessionStatus;
import org.example.userservice2.Models.User;
import org.example.userservice2.Repositories.SessionRepository;
import org.example.userservice2.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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
    private SessionRepository sessionRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public User signUp(String email, String password) {
        User user = new User();
        user.setEmail(email);

        user.setPassword(bCryptPasswordEncoder.encode(password));

        User savedUser = userRepository.save(user);
        return savedUser;
    }

    public Pair<User,MultiValueMap<String,String>> login(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if(userOptional.isEmpty()) {
            return null;
        }

        User user = userOptional.get();
        if(!bCryptPasswordEncoder.matches(password,user.getPassword())) {
            return null;
        }

        /**
         *  DEMO - 1  : USE HARCODE CONTENT AND GENERATE TOKEN WITHOUT SIGNATURE
         *  DEMO - 2  : USE HARDCODE CONTENT AND GENRATE TOKEN WITH SIGNATURE USING ALGO AND SECRET
         *  DEMO - 3 : USE USER INFO CLAIMS MAP AND GENERATE TOKEN
         *  DEMO - 4 : STORE TOKEN IN SESSION AND RETURN PAIR<USER,HEADERS>
         *  DEMO - 5 : SIGN AND VALIDATE AS WELL
         */

        //DEMO - 1
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

        //DEMO-2
        MacAlgorithm algorithm = Jwts.SIG.HS256;
        SecretKey secretKey = algorithm.key().build();
        //String token = Jwts.builder().content(content).signWith(secretKey,algorithm).compact();


        //DEMO-3
        Map<String,Object> jwtData = new HashMap<>();
        jwtData.put("email",user.getEmail());
        jwtData.put("roles",user.getRoles());
        long nowInMillis = System.currentTimeMillis();
        jwtData.put("expiryTime",new Date(nowInMillis+10000));
        jwtData.put("createdAt",new Date(nowInMillis));
        String token = Jwts.builder().claims(jwtData).signWith(secretKey).compact();

        //DEMO-4
        Session session = new Session();
        session.setSessionStatus(SessionStatus.ACTIVE);
        session.setToken(token);
        session.setUser(user);
        sessionRepository.save(session);

        MultiValueMap<String,String> headers = new LinkedMultiValueMap<>();
        headers.add(HttpHeaders.SET_COOKIE,"auth-token:"+token);
        //headers.add(HttpHeaders.ACCEPT,"anuragkhanna");
        Pair<User,MultiValueMap<String,String>> userWithHeaders = new Pair<>(user,headers);
        return userWithHeaders;
    }
}
