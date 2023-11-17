package com.novmah.bankingapp.service;

import com.novmah.bankingapp.dto.request.ChangePasswordRequest;
import com.novmah.bankingapp.dto.request.EnquiryRequest;
import com.novmah.bankingapp.dto.request.UserRequest;
import com.novmah.bankingapp.dto.response.BankResponse;
import com.novmah.bankingapp.dto.response.UserResponse;

import java.util.List;

public interface UserService {

    BankResponse balanceEnquiry(EnquiryRequest enquiryRequest);
    String nameEnquiry(EnquiryRequest enquiryRequest);

    UserResponse getUser();

    UserResponse updateUser(UserRequest userRequest);

    void changePassword(ChangePasswordRequest changePasswordRequest);

    String deleteUser();

    List<UserResponse> getALlUsers();



    String deleteUserByAccountNumber(String accountNumber);
}
