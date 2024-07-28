package com.jwt.JwtSecurity.config;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demoControl")
public class DemoController {

    @GetMapping("/demo")
    public String demo(){
        return "Welcome to secured point";
    }
}
