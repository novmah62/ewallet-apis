package com.novmah.bankingapp.repository;

import com.novmah.bankingapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByEmail(String email);
    Boolean existsByAccountNumber(String accountNumber);
    Optional<User> findByAccountNumber(String accountNumber);
    void deleteByAccountNumber(String accountNumber);

}
