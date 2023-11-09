package com.novmah.bankingapp.service.impl;

import com.novmah.bankingapp.dto.AccountInfo;
import com.novmah.bankingapp.dto.BankResponse;
import com.novmah.bankingapp.dto.EnquiryRequest;
import com.novmah.bankingapp.entity.User;
import com.novmah.bankingapp.exception.ResourceNotFoundException;
import com.novmah.bankingapp.repository.UserRepository;
import com.novmah.bankingapp.service.UserService;
import com.novmah.bankingapp.utils.AccountUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public BankResponse balanceEnquiry(EnquiryRequest enquiryRequest) {
        boolean isAccountExist = userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber());
        if (!isAccountExist) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null).build();
        }
        User user = userRepository.findByAccountNumber(enquiryRequest.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("User", "account number", enquiryRequest.getAccountNumber()));
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_FOUND_SUCCESS_CODE)
                .responseMessage(AccountUtils.ACCOUNT_FOUND_SUCCESS_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountNumber(user.getAccountNumber())
                        .accountName(user.getFirstName() + " " + user.getLastName())
                        .accountBalance(user.getAccountBalance()).build()).build();
    }

    @Override
    @Transactional(readOnly = true)
    public String nameEnquiry(EnquiryRequest enquiryRequest) {
        boolean isAccountExist = userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber());
        if (!isAccountExist) {
            return AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE;
        }
        User user = userRepository.findByAccountNumber(enquiryRequest.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("User", "account number", enquiryRequest.getAccountNumber()));
        return user.getFirstName() + " " + user.getLastName();
    }

}
