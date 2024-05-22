package com.novmah.bankingapp.controller;

import com.novmah.bankingapp.dto.request.UserRequest;
import com.novmah.bankingapp.dto.response.ApiResponse;
import com.novmah.bankingapp.dto.response.UserResponse;
import com.novmah.bankingapp.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "User Account Management APIs")
public class UserController {

    private final UserService userService;

    @GetMapping("/nameEnquiry/{accountNumber}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse nameEnquiry(@PathVariable String accountNumber) {
        return new ApiResponse(true, userService.nameEnquiry(accountNumber));
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

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<UserResponse> getAllUsers() {
        return userService.getALlUsers();
    }

    @GetMapping("/clear_cache")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ApiResponse clearCache() {
        return new ApiResponse(true, userService.clearCache());
    }

}
