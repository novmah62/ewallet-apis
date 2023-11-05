package com.novmah.bankingapp.service.impl;

import com.novmah.bankingapp.dto.*;
import com.novmah.bankingapp.entity.User;
import com.novmah.bankingapp.repository.UserRepository;
import com.novmah.bankingapp.service.EmailService;
import com.novmah.bankingapp.service.UserService;
import com.novmah.bankingapp.utils.AccountUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EmailService emailService;

    @Override
    public BankResponse createAccount(UserRequest userRequest) {

        if (userRepository.existsByEmail(userRequest.getEmail())) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User user = userRepository.save(User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .gender(userRequest.getGender())
                .address(userRequest.getAddress())
                .stateOfOrigin(userRequest.getStateOfOrigin())
                .accountNumber(AccountUtils.generateAccountNumber())
                .accountBalance(BigDecimal.ZERO)
                .email(userRequest.getEmail())
                .phoneNumber(userRequest.getPhoneNumber())
                .alternativePhoneNumber(userRequest.getAlternativePhoneNumber())
                .status("ACTIVE").build());
        emailService.sendEmailAlert(EmailDetails.builder()
                .recipient(user.getEmail())
                .subject("ACCOUNT CREATION")
                .messageBody("Congratulations! Your account has been successfully created.\n" +
                        "Your Account Details: \n" +
                        "Account Name: " + user.getFirstName() + " " + user.getLastName() + "\n" +
                        "Account Number: " + user.getAccountNumber())
                .build());

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS_CODE)
                .responseMessage(AccountUtils.ACCOUNT_CREATION_SUCCESS_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(user.getFirstName() + " " + user.getLastName())
                        .accountNumber(user.getAccountNumber())
                        .accountBalance(user.getAccountBalance()).build()).build();
    }

    @Override
    public BankResponse balanceEnquiry(EnquiryRequest enquiryRequest) {
        boolean isAccountExist = userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber());
        if (!isAccountExist) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null).build();
        }
        User user = userRepository.findByAccountNumber(enquiryRequest.getAccountNumber());
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_FOUND_SUCCESS_CODE)
                .responseMessage(AccountUtils.ACCOUNT_FOUND_SUCCESS_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountNumber(user.getAccountNumber())
                        .accountName(user.getFirstName() + " " + user.getLastName())
                        .accountBalance(user.getAccountBalance()).build()).build();
    }

    @Override
    public String nameEnquiry(EnquiryRequest enquiryRequest) {
        boolean isAccountExist = userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber());
        if (!isAccountExist) {
            return AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE;
        }
        User user = userRepository.findByAccountNumber(enquiryRequest.getAccountNumber());
        return user.getFirstName() + user.getLastName();
    }


}
