package com.novmah.bankingapp.service.impl;

import com.novmah.bankingapp.dto.request.UserRequest;
import com.novmah.bankingapp.dto.response.UserResponse;
import com.novmah.bankingapp.entity.Account;
import com.novmah.bankingapp.entity.User;
import com.novmah.bankingapp.exception.ResourceNotFoundException;
import com.novmah.bankingapp.mapper.UserMapper;
import com.novmah.bankingapp.repository.AccountRepository;
import com.novmah.bankingapp.repository.UserRepository;
import com.novmah.bankingapp.service.AuthService;
import com.novmah.bankingapp.service.UserService;
import com.novmah.bankingapp.utils.BankUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final AuthService authService;
    private final UserMapper mapper;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "#accountNumber + 'name'")
    public String nameEnquiry(String accountNumber) {
        boolean isAccountExist = accountRepository.existsByAccountNumber(accountNumber);
        if (!isAccountExist) {
            return BankUtils.ACCOUNT_NOT_EXIST_MESSAGE;
        }
        User user = userRepository.findByAccount_AccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("User", "account number", accountNumber));
        return user.getFirstName() + " " + user.getLastName();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUser() {
        Account account = authService.getCurrentAccount();
        User user = userRepository.findByAccount_AccountNumber(account.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("User", "account number", account.getAccountNumber()));
        return mapper.mapToDto(user);
    }

    @Override
    @CacheEvict(value = "users", allEntries = true)
    public UserResponse updateUser(UserRequest userRequest) {
        Account account = authService.getCurrentAccount();
        User user = userRepository.findByAccount_AccountNumber(account.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("User", "account number", account.getAccountNumber()));
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setGender(userRequest.getGender());
        user.setAddress(userRequest.getAddress());
        user.setStateOfOrigin(userRequest.getStateOfOrigin());
        user.setEmail(userRequest.getEmail());
        user.setPhoneNumber(userRequest.getPhoneNumber());
        user.setAlternativePhoneNumber(userRequest.getAlternativePhoneNumber());
        userRepository.save(user);
        return mapper.mapToDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "'all'")
    public List<UserResponse> getALlUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(mapper::mapToDto).toList();
    }

    @Override
    @CacheEvict(value = "users", allEntries = true)
    public String clearCache() {
        return "Cache has been cleared";
    }

}
