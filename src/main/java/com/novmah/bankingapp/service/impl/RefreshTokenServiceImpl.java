package com.novmah.bankingapp.service.impl;

import com.novmah.bankingapp.entity.RefreshToken;
import com.novmah.bankingapp.exception.ResourceNotFoundException;
import com.novmah.bankingapp.repository.RefreshTokenRepository;
import com.novmah.bankingapp.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public RefreshToken generateRefreshToken() {
        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .createdDate(Instant.now()).build();
        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public void validateRefreshToken(String token) {
        refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid refresh token"));
    }

    @Override
    public void deleteRefreshToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }
}
