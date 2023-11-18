package com.novmah.bankingapp.service;

import com.novmah.bankingapp.dto.request.LoginRequest;
import com.novmah.bankingapp.dto.request.RefreshTokenRequest;
import com.novmah.bankingapp.dto.request.RegisterRequest;
import com.novmah.bankingapp.dto.response.AuthenticationResponse;
import com.novmah.bankingapp.dto.response.BankResponse;
import com.novmah.bankingapp.entity.User;

public interface AuthService {

    BankResponse signup(RegisterRequest registerRequest);
    User getCurrentUser();
    String verifyAccount(String token);
    AuthenticationResponse login(LoginRequest loginRequest);
    AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest);
    String logout(RefreshTokenRequest refreshTokenRequest);

}
