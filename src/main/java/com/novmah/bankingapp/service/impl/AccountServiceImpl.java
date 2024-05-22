package com.novmah.bankingapp.service.impl;

import com.novmah.bankingapp.dto.request.ChangePasswordRequest;
import com.novmah.bankingapp.dto.response.AccountInfo;
import com.novmah.bankingapp.entity.Account;
import com.novmah.bankingapp.exception.ResourceNotFoundException;
import com.novmah.bankingapp.mapper.AccountMapper;
import com.novmah.bankingapp.repository.AccountRepository;
import com.novmah.bankingapp.service.AccountService;
import com.novmah.bankingapp.service.AuthService;
import com.novmah.bankingapp.utils.BankUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;
    private final AccountMapper mapper;


    @Override
    @Transactional(readOnly = true)
    public String balanceEnquiry(String accountNumber) {
        boolean isAccountExist = accountRepository.existsByAccountNumber(accountNumber);
        if (!isAccountExist) {
            return BankUtils.ACCOUNT_NOT_EXIST_MESSAGE;
        }
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "account number", accountNumber));

        return account.getAccountBalance().toString();
    }

    @Override
    public AccountInfo getAccount() {
        Account account = authService.getCurrentAccount();
        return mapper.mapToDto(account);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "accounts", key = "'all'")
    public List<AccountInfo> getAllAccount() {
        List<Account> accounts = accountRepository.findAll();
        return accounts.stream().map(mapper::mapToDto).toList();
    }

    @Override
    public String changePassword(ChangePasswordRequest changePasswordRequest) {
        Account account = authService.getCurrentAccount();
        if (!passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), account.getPassword())) {
            throw new ResourceNotFoundException("Wrong password");
        }
        if (!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmationPassword())) {
            throw new ResourceNotFoundException("Password are not the same");
        }

        account.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        accountRepository.save(account);
        return "Password changed successfully";
    }

    @Override
    @CacheEvict(value = "accounts", allEntries = true)
    public String deleteAccount() {
        Account account = authService.getCurrentAccount();
        accountRepository.deleteByAccountNumber(account.getAccountNumber());
        return "deleted account: " + account.getAccountNumber();
    }

    @Override
    @CacheEvict(value = "accounts", allEntries = true)
    public String deleteAccountByAccountNumber(String accountNumber) {
        accountRepository.deleteByAccountNumber(accountNumber);
        return "deleted account: " + accountNumber;
    }

    @Override
    @CacheEvict(value = "accounts", allEntries = true)
    public String clearCache() {
        return "Cache has been cleared";
    }

}
