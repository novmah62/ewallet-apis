package com.novmah.bankingapp.service.impl;

import com.novmah.bankingapp.dto.request.LoginRequest;
import com.novmah.bankingapp.dto.request.RefreshTokenRequest;
import com.novmah.bankingapp.dto.request.RegisterRequest;
import com.novmah.bankingapp.dto.response.AccountInfo;
import com.novmah.bankingapp.dto.response.AuthenticationResponse;
import com.novmah.bankingapp.dto.response.BankResponse;
import com.novmah.bankingapp.dto.response.EmailDetails;
import com.novmah.bankingapp.entity.Account;
import com.novmah.bankingapp.entity.Role;
import com.novmah.bankingapp.entity.User;
import com.novmah.bankingapp.entity.VerificationToken;
import com.novmah.bankingapp.exception.ResourceNotFoundException;
import com.novmah.bankingapp.repository.AccountRepository;
import com.novmah.bankingapp.repository.UserRepository;
import com.novmah.bankingapp.repository.VerificationTokenRepository;
import com.novmah.bankingapp.security.JwtProvider;
import com.novmah.bankingapp.service.AuthService;
import com.novmah.bankingapp.service.MailService;
import com.novmah.bankingapp.service.RefreshTokenService;
import com.novmah.bankingapp.utils.BankUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final JwtProvider jwtProvider;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final MailService mailService;

    @Override
    public BankResponse signup(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return BankResponse.builder()
                    .responseStatus("ERROR")
                    .responseMessage(BankUtils.ACCOUNT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        Account account = accountRepository.save(Account.builder()
                .accountNumber(BankUtils.generateAccountNumber())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .accountBalance(BigDecimal.ZERO)
                .role(Role.ROLE_USER)
                .status("OFFLINE")
                .enabled(false)
                .build());

        User user = userRepository.save(User.builder()
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .gender(registerRequest.getGender())
                .address(registerRequest.getAddress())
                .stateOfOrigin(registerRequest.getStateOfOrigin())
                .email(registerRequest.getEmail())
                .phoneNumber(registerRequest.getPhoneNumber())
                .alternativePhoneNumber(registerRequest.getAlternativePhoneNumber())
                .build());

        String token = generateVerificationToken(account);
        log.info("VERIFICATION TOKEN: {}", token);
        mailService.sendEmailAlert(EmailDetails.builder()
                .recipient(user.getEmail())
                .subject("ACCOUNT CREATION")
                .messageBody("Congratulations! Your account has been successfully created.\n" +
                        "Your Account Details: \n" +
                        "Account Name: " + user.getFirstName() + " " + user.getLastName() + "\n" +
                        "Account Number: " + account.getAccountNumber() + "\n" +
                        "please click on the below url to activate your account : " +
                        "http://localhost:8080/api/auth/accountVerification/" + token)
                .build());

        return BankResponse.builder()
                .responseStatus("SUCCESS")
                .responseMessage(BankUtils.ACCOUNT_CREATION_SUCCESS_MESSAGE)
                .userName(user.getFirstName() + " " + user.getLastName())
                .accountInfo(AccountInfo.builder()
                        .accountNumber(account.getAccountNumber())
                        .accountBalance(account.getAccountBalance())
                        .status(account.getStatus()).build()).build();

    }
    public String generateVerificationToken(Account account) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = VerificationToken.builder()
                .token(token)
                .expiryDate(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
                .account(account).build();
        verificationTokenRepository.save(verificationToken);
        return token;
    }

    @Override
    @Transactional(readOnly = true)
    public Account getCurrentAccount() {
        Jwt principal = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return accountRepository.findByAccountNumber(principal.getSubject())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid account"));
    }

    @Override
    public String verifyAccount(String token) {
        Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(token);
        fetchUserAndEnable(verificationToken.orElseThrow(() -> new ResourceNotFoundException("Invalid token")));
        return "Account Activated Successfully";
    }
    public void fetchUserAndEnable(VerificationToken verificationToken) {
        String accountNumber = verificationToken.getAccount().getAccountNumber();
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "account number", accountNumber));
        account.setEnabled(true);
        accountRepository.save(account);
    }

    @Override
    public AuthenticationResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getAccountNumber(),
                loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtProvider.generateToken(authentication);
        Account account = accountRepository.findByAccountNumber(loginRequest.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "account number", loginRequest.getAccountNumber()));
        account.setStatus("ACTIVE");
        accountRepository.save(account);
        return AuthenticationResponse.builder()
                .authenticationToken(token)
                .refreshToken(refreshTokenService.generateRefreshToken().getToken())
                .expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
                .accountNumber(loginRequest.getAccountNumber()).build();
    }

    @Override
    public AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        refreshTokenService.validateRefreshToken(refreshTokenRequest.getRefreshToken());
        String token = jwtProvider.generateTokenAccountNumber(refreshTokenRequest.getAccountNumber());
        return AuthenticationResponse.builder()
                .authenticationToken(token)
                .refreshToken(refreshTokenRequest.getRefreshToken())
                .expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
                .accountNumber(refreshTokenRequest.getAccountNumber()).build();
    }

    @Override
    public String logout(RefreshTokenRequest refreshTokenRequest) {
        refreshTokenService.deleteRefreshToken(refreshTokenRequest.getRefreshToken());
        Account account = accountRepository.findByAccountNumber(refreshTokenRequest.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("User", "account number", refreshTokenRequest.getAccountNumber()));
        account.setStatus("OFFLINE");
        accountRepository.save(account);
        return "Refresh Token Deleted Successfully!!!";
    }

}
