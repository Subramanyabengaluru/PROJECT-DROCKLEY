package com.platform.drockley.auth.service.impl;

import com.platform.drockley.auth.dto.AuthResponse;
import com.platform.drockley.auth.dto.LoginRequest;
import com.platform.drockley.auth.dto.RegisterRequest;
import com.platform.drockley.auth.service.AuthService;
import com.platform.drockley.common.exception.BadRequestException;
import com.platform.drockley.common.exception.NotFoundException;
import com.platform.drockley.entity.User;
import com.platform.drockley.enums.Role;
import com.platform.drockley.enums.UserStatus;
import com.platform.drockley.repository.UserRepository;
import com.platform.drockley.security.JwtTokenProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already in use");
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .role(Role.ROLE_USER)
                .status(UserStatus.ACTIVE)
                .emailVerified(false)
                .build();

        userRepository.save(user);

        String access = jwtTokenProvider.generateToken(user.getId().toString(), user.getRole().name(), 60 * 24);
        String refresh = jwtTokenProvider.generateToken(user.getId().toString(), user.getRole().name(), 60L * 24 * 30);

        return new AuthResponse(access, refresh);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail().toLowerCase(), request.getPassword())
        );

        String email = (String) auth.getPrincipal();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));

        String access = jwtTokenProvider.generateToken(user.getId().toString(), user.getRole().name(), 60 * 24);
        String refresh = jwtTokenProvider.generateToken(user.getId().toString(), user.getRole().name(), 60L * 24 * 30);

        return new AuthResponse(access, refresh);
    }

    @Override
    public AuthResponse refresh(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BadRequestException("Invalid refresh token");
        }

        var claims = jwtTokenProvider.parseClaims(refreshToken);
        String userId = claims.getSubject();
        User user = userRepository.findById(UUID.fromString(userId)).orElseThrow(() -> new NotFoundException("User not found"));

        String access = jwtTokenProvider.generateToken(user.getId().toString(), user.getRole().name(), 60 * 24);
        String refresh = jwtTokenProvider.generateToken(user.getId().toString(), user.getRole().name(), 60L * 24 * 30);

        return new AuthResponse(access, refresh);
    }
}
