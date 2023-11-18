package com.novmah.bankingapp.service;

import com.novmah.bankingapp.dto.request.ChangePasswordRequest;
import com.novmah.bankingapp.dto.request.UserRequest;
import com.novmah.bankingapp.dto.response.BankResponse;
import com.novmah.bankingapp.dto.response.UserResponse;

import java.util.List;

public interface UserService {

    BankResponse balanceEnquiry(String accountNumber);
    String nameEnquiry(String accountNumber);

    UserResponse getUser();

    UserResponse updateUser(UserRequest userRequest);

    String changePassword(ChangePasswordRequest changePasswordRequest);

    String deleteUser();

    List<UserResponse> getALlUsers();

    String deleteUserByAccountNumber(String accountNumber);

    String clearCache();
}
