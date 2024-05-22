package com.novmah.bankingapp.service;

import com.novmah.bankingapp.dto.request.ChangePasswordRequest;
import com.novmah.bankingapp.dto.response.AccountInfo;

import java.util.List;

public interface AccountService {

    String balanceEnquiry(String accountNumber);
    AccountInfo getAccount();
    List<AccountInfo> getAllAccount();
    String changePassword(ChangePasswordRequest changePasswordRequest);
    String deleteAccount();
    String deleteAccountByAccountNumber(String accountNumber);
    String clearCache();

}
