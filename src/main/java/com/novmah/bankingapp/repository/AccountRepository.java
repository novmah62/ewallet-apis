package com.novmah.bankingapp.repository;

import com.novmah.bankingapp.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountNumber(String accountNumber);
    Boolean existsByPin(String pin);
    Boolean existsByAccountNumber(String accountNumber);
    void deleteByAccountNumber(String accountNumber);
}
