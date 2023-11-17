package com.novmah.bankingapp.controller;

import com.novmah.bankingapp.dto.request.ChangePasswordRequest;
import com.novmah.bankingapp.dto.request.UserRequest;
import com.novmah.bankingapp.dto.response.ApiResponse;
import com.novmah.bankingapp.dto.response.BankResponse;
import com.novmah.bankingapp.dto.request.EnquiryRequest;
import com.novmah.bankingapp.dto.response.UserResponse;
import com.novmah.bankingapp.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public UserResponse getUser() {
        return userService.getUser();
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public UserResponse updateUser(@Valid @RequestBody UserRequest request) {
        return userService.updateUser(request);
    }

    @PatchMapping
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        userService.changePassword(request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse deleteUser() {
        return new ApiResponse(true, userService.deleteUser());
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<UserResponse> getAllUsers() {
        return userService.getALlUsers();
    }

    @DeleteMapping("/{accountNumber}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ApiResponse deleteUserByAccountNumber(@PathVariable String accountNumber) {
        return new ApiResponse(true, userService.deleteUserByAccountNumber(accountNumber));
    }

}
