package com.jwt.JwtSecurity.service;

import com.jwt.JwtSecurity.dto.UserRequest;
import com.jwt.JwtSecurity.dto.UserResponse;
import com.jwt.JwtSecurity.model.RefreshToken;
import com.jwt.JwtSecurity.model.Role;
import com.jwt.JwtSecurity.model.User;
import com.jwt.JwtSecurity.repository.RefreshTokenRepository;
import com.jwt.JwtSecurity.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    UserRepo userRepo;

    @Autowired
    JwtService jwtService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Value("${my.app.refreshExpirationMs}")
    Integer refreshExpirationMs;

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    public UserResponse register(UserRequest users) {

        User user = new User();
        user.setUsername(users.getUsername());
        user.setEmail(users.getEmail());
        user.setPassword(passwordEncoder.encode(users.getPassword()));
        user.setRole(Role.valueOf(users.getRole()));
        userRepo.save(user);
        String accessToken = jwtService.generateAccessToken(user.getEmail());
        String refreshToken = generateRefreshToken(user);

        return UserResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build();
    }

    private String generateRefreshToken(User user) {
        RefreshToken previousToken = refreshTokenRepository.findByUserId(user.getId()).get();
        refreshTokenRepository.delete(previousToken);
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpirydate(Instant.now().plusMillis(refreshExpirationMs));
        refreshToken.setUser(user);

        refreshTokenRepository.save(refreshToken);
        return refreshToken.getToken();

    }

    public UserResponse login(UserRequest user) {
       User users = userRepo.findByEmail(user.getEmail()).get();
        String accessToken = jwtService.generateAccessToken(user.getEmail());
        String refreshToken = generateRefreshToken(users);
        return UserResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build();
    }

    public UserResponse getAccessToken(String refreshToken) {
        Optional<RefreshToken> getToken = refreshTokenRepository.findByToken(refreshToken);
        RefreshToken refreshToken1 = verifyRefreshTokenExpiration(getToken.get());
        User user = userRepo.findById(getToken.get().getUser().getId()).get();
        String accessToken = jwtService.generateAccessToken(user.getEmail());
        return UserResponse.builder().accessToken(accessToken).refreshToken(refreshToken1.getToken()).build();

    }

    private RefreshToken verifyRefreshTokenExpiration(RefreshToken refreshToken) {
        if (refreshToken.getExpirydate().compareTo(Instant.now())< 0 ){
        refreshTokenRepository.delete(refreshToken);
        throw new RuntimeException("Token Expired please login");
        }
        return refreshToken;
    }
}
