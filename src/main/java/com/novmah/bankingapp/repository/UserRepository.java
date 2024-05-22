package com.novmah.bankingapp.repository;

import com.novmah.bankingapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByEmail(String email);

    Optional<User> findByAccount_AccountNumber(String accountNumber);

}
