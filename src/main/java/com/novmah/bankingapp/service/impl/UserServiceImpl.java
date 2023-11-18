package com.novmah.bankingapp.service.impl;

import com.novmah.bankingapp.dto.request.ChangePasswordRequest;
import com.novmah.bankingapp.dto.request.UserRequest;
import com.novmah.bankingapp.dto.response.AccountInfo;
import com.novmah.bankingapp.dto.response.BankResponse;
import com.novmah.bankingapp.dto.response.UserResponse;
import com.novmah.bankingapp.entity.User;
import com.novmah.bankingapp.exception.ResourceNotFoundException;
import com.novmah.bankingapp.mapper.UserMapper;
import com.novmah.bankingapp.repository.UserRepository;
import com.novmah.bankingapp.service.AuthService;
import com.novmah.bankingapp.service.UserService;
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
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper mapper;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "#accountNumber")
    public BankResponse balanceEnquiry(String accountNumber) {
        boolean isAccountExist = userRepository.existsByAccountNumber(accountNumber);
        if (!isAccountExist) {
            return BankResponse.builder()
                    .responseStatus("ERROR")
                    .responseMessage(BankUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null).build();
        }
        User user = userRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("User", "account number", accountNumber));
        return BankResponse.builder()
                .responseStatus("SUCCESS")
                .responseMessage(BankUtils.ACCOUNT_FOUND_SUCCESS_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountNumber(user.getAccountNumber())
                        .accountName(user.getFirstName() + " " + user.getLastName())
                        .accountBalance(user.getAccountBalance()).build()).build();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "#accountNumber + 'name'")
    public String nameEnquiry(String accountNumber) {
        boolean isAccountExist = userRepository.existsByAccountNumber(accountNumber);
        if (!isAccountExist) {
            return BankUtils.ACCOUNT_NOT_EXIST_MESSAGE;
        }
        User user = userRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("User", "account number", accountNumber));
        return user.getFirstName() + " " + user.getLastName();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUser() {
        User user = authService.getCurrentUser();
        return mapper.mapToDto(user);
    }

    @Override
    @CacheEvict(value = "users", allEntries = true)
    public UserResponse updateUser(UserRequest userRequest) {
        User user = authService.getCurrentUser();
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
    public String changePassword(ChangePasswordRequest changePasswordRequest) {
        User user = authService.getCurrentUser();
        if (!passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), user.getPassword())) {
            throw new ResourceNotFoundException("Wrong password");
        }
        if (!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmationPassword())) {
            throw new ResourceNotFoundException("Password are not the same");
        }

        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(user);
        return "Password changed successfully";
    }

    @Override
    @CacheEvict(value = "users", allEntries = true)
    public String deleteUser() {
        User user = authService.getCurrentUser();
        userRepository.deleteById(user.getId());
        return "User Deleted Successfully!!!";
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
    public String deleteUserByAccountNumber(String accountNumber) {
        userRepository.deleteByAccountNumber(accountNumber);
        return "deleted account: " + accountNumber;
    }

    @Override
    @CacheEvict(value = "users", allEntries = true)
    public String clearCache() {
        return "Cache has been cleared";
    }

}
