package com.novmah.bankingapp.service.impl;

import com.novmah.bankingapp.exception.ResourceNotFoundException;
import com.novmah.bankingapp.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return accountRepository.findByAccountNumber(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "account number", username));
    }
}
