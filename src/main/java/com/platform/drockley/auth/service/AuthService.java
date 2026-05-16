package com.platform.drockley.auth.service;

import com.platform.drockley.auth.dto.AuthResponse;
import com.platform.drockley.auth.dto.LoginRequest;
import com.platform.drockley.auth.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refresh(String refreshToken);
}
