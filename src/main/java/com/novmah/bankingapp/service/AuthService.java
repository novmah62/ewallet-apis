package com.novmah.bankingapp.service;

import com.novmah.bankingapp.dto.*;
import com.novmah.bankingapp.entity.User;

public interface AuthService {

    BankResponse signup(UserRequest userRequest);

    User getCurrentUser();

    String verifyAccount(String token);

    AuthenticationResponse login(LoginRequest loginRequest);

    AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest);

    String logout(RefreshTokenRequest refreshTokenRequest);

}
