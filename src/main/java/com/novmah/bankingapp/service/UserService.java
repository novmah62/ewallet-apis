package com.novmah.bankingapp.service;

import com.novmah.bankingapp.dto.request.UserRequest;
import com.novmah.bankingapp.dto.response.UserResponse;

import java.util.List;

public interface UserService {

    String nameEnquiry(String accountNumber);
    UserResponse getUser();
    UserResponse updateUser(UserRequest userRequest);
    List<UserResponse> getALlUsers();
    String clearCache();

}
