package com.novmah.bankingapp.service;

import com.novmah.bankingapp.entity.RefreshToken;

public interface RefreshTokenService {

    RefreshToken generateRefreshToken();

    void validateRefreshToken(String token);

    void deleteRefreshToken(String token);

}
