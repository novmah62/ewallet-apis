package com.novmah.bankingapp.controller;

import com.novmah.bankingapp.dto.ApiResponse;
import com.novmah.bankingapp.dto.BankResponse;
import com.novmah.bankingapp.dto.EnquiryRequest;
import com.novmah.bankingapp.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "User Account Management APIs")
public class UserController {

    private final UserService userService;
    @GetMapping("/balanceEnquiry")
    @ResponseStatus(HttpStatus.OK)
    public BankResponse balanceEnquiry(@Valid @RequestBody EnquiryRequest request) {
        return userService.balanceEnquiry(request);
    }

    @GetMapping("/nameEnquiry")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse nameEnquiry(@Valid @RequestBody EnquiryRequest request) {
        return new ApiResponse(true,userService.nameEnquiry(request));
    }

}
