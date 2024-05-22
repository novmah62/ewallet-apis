package com.novmah.bankingapp.service.impl;

import com.novmah.bankingapp.entity.Account;
import com.novmah.bankingapp.exception.ResourceNotFoundException;
import com.novmah.bankingapp.repository.AccountRepository;
import com.novmah.bankingapp.service.AuthService;
import com.novmah.bankingapp.service.PinService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PinServiceImpl implements PinService {

    private final AccountRepository accountRepository;
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public String isPinCreated() {
        Account account = authService.getCurrentAccount();
        if (accountRepository.existsByPin(account.getPin())) {
            return "PIN created!";
        }
        return "PIN No Created!";
    }

    @Override
    public void createPin(String accountNumber, String password, String pin) {

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "account number", accountNumber));
        if (!passwordEncoder.matches(password, account.getPassword())) {
            throw new ResourceNotFoundException("Invalid password");
        }
        account.setPin(passwordEncoder.encode(pin));
        accountRepository.save(account);
    }

    @Override
    public void updatePin(String accountNumber, String oldPin, String password, String newPin) {

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "account number", accountNumber));
        if (!passwordEncoder.matches(oldPin, account.getPin())) {
            throw new ResourceNotFoundException("Invalid PIN");
        }
        if (!passwordEncoder.matches(password, account.getPassword())) {
            throw new ResourceNotFoundException("Invalid password");
        }
        account.setPin(passwordEncoder.encode(newPin));
        accountRepository.save(account);
    }
}
