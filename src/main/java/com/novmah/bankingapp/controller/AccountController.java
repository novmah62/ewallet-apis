package com.novmah.bankingapp.controller;

import com.novmah.bankingapp.dto.request.ChangePasswordRequest;
import com.novmah.bankingapp.dto.response.AccountInfo;
import com.novmah.bankingapp.service.AccountService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
@Tag(name = "Account Management APIs")
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/balanceEnquiry/{accountNumber}")
    @ResponseStatus(HttpStatus.OK)
    public String balanceEnquiry(@PathVariable String accountNumber) {
        return accountService.balanceEnquiry(accountNumber);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public AccountInfo getAccount() {
        return accountService.getAccount();
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<AccountInfo> getAllAccount() {
        return accountService.getAllAccount();
    }

    @PatchMapping("/changePassword")
    @ResponseStatus(HttpStatus.OK)
    public String changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        return accountService.changePassword(changePasswordRequest);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public String deleteAccount() {
        return accountService.deleteAccount();
    }

    @DeleteMapping("/{accountNumber}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String deleteAccountByAccountNumber(@PathVariable String accountNumber) {
        return accountService.deleteAccountByAccountNumber(accountNumber);
    }

    @GetMapping("/clear_cache")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String clearCache() {
        return accountService.clearCache();
    }

}
