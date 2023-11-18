package com.novmah.bankingapp.controller;

import com.novmah.bankingapp.dto.request.LoginRequest;
import com.novmah.bankingapp.dto.request.RefreshTokenRequest;
import com.novmah.bankingapp.dto.request.RegisterRequest;
import com.novmah.bankingapp.dto.response.ApiResponse;
import com.novmah.bankingapp.dto.response.AuthenticationResponse;
import com.novmah.bankingapp.dto.response.BankResponse;
import com.novmah.bankingapp.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication Management APIs")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public BankResponse signup(@Valid @RequestBody RegisterRequest request) {
        return authService.signup(request);
    }

    @GetMapping("/accountVerification/{token}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse verifyAccount(@PathVariable String token) {
        return new ApiResponse(true, authService.verifyAccount(token));
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public AuthenticationResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh/token")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthenticationResponse refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return authService.refreshToken(request);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse logout(@Valid @RequestBody RefreshTokenRequest request) {
        return new ApiResponse(true, authService.logout(request));
    }

}
