package com.novmah.bankingapp.repository;

import com.novmah.bankingapp.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
    List<Transaction> findByAccountNumber(String accountNumber);
    Page<Transaction> findByAccountNumber(String accountNumber, Pageable pageable);

}
