package com.novmah.bankingapp.controller;

import com.novmah.bankingapp.dto.*;
import com.novmah.bankingapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "User Account Management APIs")
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Create New User Account",
            description = "Creating a new user and assigning an account ID"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Http status 201 CREATE"
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BankResponse createAccount(@RequestBody UserRequest request) {
        return userService.createAccount(request);
    }

    @Operation(
            summary = "Balance Enquiry",
            description = "Given an account number, check how much the user has"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Http status 200 SUCCESS"
    )
    @GetMapping("/balanceEnquiry")
    @ResponseStatus(HttpStatus.OK)
    public BankResponse balanceEnquiry(@RequestBody EnquiryRequest request) {
        return userService.balanceEnquiry(request);
    }

    @GetMapping("/nameEnquiry")
    @ResponseStatus(HttpStatus.OK)
    public String nameEnquiry(@RequestBody EnquiryRequest request) {
        return userService.nameEnquiry(request);
    }


}
