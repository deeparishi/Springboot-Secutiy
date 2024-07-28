package com.jwt.JwtSecurity.controller;

import com.jwt.JwtSecurity.dto.UserRequest;
import com.jwt.JwtSecurity.dto.UserResponse;
import com.jwt.JwtSecurity.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/userController")
public class UserContoller {

    @Autowired
    UserService userService;

    @PostMapping("/register")
    public UserResponse register(@RequestBody UserRequest user){
       UserResponse token = userService.register(user);
       return token;
    }

    @PostMapping("/login")
    public UserResponse login(@RequestBody UserRequest user){
        UserResponse token = userService.login(user);
        return token;
    }

    @GetMapping("/getToken/{refreshToken}")
    public UserResponse getToken(@PathVariable("refreshToken") String refreshToken){
        UserResponse token = userService.getAccessToken(refreshToken);
        return token;
    }
}
