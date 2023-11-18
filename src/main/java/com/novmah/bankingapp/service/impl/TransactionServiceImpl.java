package com.novmah.bankingapp.service.impl;

import com.novmah.bankingapp.dto.request.TransactionRequest;
import com.novmah.bankingapp.dto.response.*;
import com.novmah.bankingapp.entity.Transaction;
import com.novmah.bankingapp.entity.User;
import com.novmah.bankingapp.exception.ResourceNotFoundException;
import com.novmah.bankingapp.mapper.TransactionMapper;
import com.novmah.bankingapp.repository.TransactionRepository;
import com.novmah.bankingapp.repository.UserRepository;
import com.novmah.bankingapp.service.AuthService;
import com.novmah.bankingapp.service.MailService;
import com.novmah.bankingapp.service.TransactionService;
import com.novmah.bankingapp.utils.BankUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final TransactionMapper mapper;
    private final AuthService authService;
    private final MailService mailService;

    @Override
    @CacheEvict(value = "transactions", allEntries = true)
    public BankResponse credit(TransactionRequest transactionRequest) {
        boolean isAccountExist = userRepository.existsByAccountNumber(transactionRequest.getAccountNumber());
        if (!isAccountExist) {
            throw new ResourceNotFoundException(BankUtils.ACCOUNT_NOT_EXIST_MESSAGE);
        }

        User user = userRepository.findByAccountNumber(transactionRequest.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("User", "account number", transactionRequest.getAccountNumber()));
        user.setAccountBalance(user.getAccountBalance().add(transactionRequest.getAmount()));
        userRepository.save(user);

        Transaction transaction = mapper.mapToEntity(transactionRequest, "CREDIT", user.getAccountNumber());
        transactionRepository.save(transaction);

        return BankResponse.builder()
                .responseStatus("SUCCESS")
                .responseMessage(BankUtils.ACCOUNT_CREDITED_SUCCESS_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountNumber(user.getAccountNumber())
                        .accountName(user.getFirstName() + " " + user.getLastName())
                        .accountBalance(user.getAccountBalance()).build()).build();
    }

    @Override
    @CacheEvict(value = "transactions", allEntries = true)
    public BankResponse debit(TransactionRequest transactionRequest) {
        boolean isAccountExist = userRepository.existsByAccountNumber(transactionRequest.getAccountNumber());
        if (!isAccountExist) {
            throw new ResourceNotFoundException(BankUtils.ACCOUNT_NOT_EXIST_MESSAGE);
        }

        User user = userRepository.findByAccountNumber(transactionRequest.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("User", "account number", transactionRequest.getAccountNumber()));
        if (transactionRequest.getAmount().compareTo(user.getAccountBalance()) > 0) {
            throw new ResourceNotFoundException(BankUtils.INSUFFICIENT_BALANCE_MESSAGE);
        } else {
            user.setAccountBalance(user.getAccountBalance().subtract(transactionRequest.getAmount()));
            userRepository.save(user);
        }

        Transaction transaction = mapper.mapToEntity(transactionRequest, "DEBIT", user.getAccountNumber());
        transactionRepository.save(transaction);

        return BankResponse.builder()
                .responseStatus("SUCCESS")
                .responseMessage(BankUtils.ACCOUNT_DEBITED_SUCCESS_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountNumber(user.getAccountNumber())
                        .accountName(user.getFirstName() + " " + user.getLastName())
                        .accountBalance(user.getAccountBalance()).build()).build();
    }

    @Override
    @CacheEvict(value = "transactions", allEntries = true)
    public TransferResponse transfer(TransactionRequest transactionRequest) {
        boolean isDestinationAccountExist = userRepository.existsByAccountNumber(transactionRequest.getAccountNumber());
        if (!isDestinationAccountExist) {
            throw new ResourceNotFoundException(BankUtils.ACCOUNT_NOT_EXIST_MESSAGE);
        }

        User sourceAccountUser = authService.getCurrentUser();
        if (transactionRequest.getAmount().compareTo(sourceAccountUser.getAccountBalance()) > 0) {
            throw new ResourceNotFoundException(BankUtils.INSUFFICIENT_BALANCE_MESSAGE);
        } else {
            sourceAccountUser.setAccountBalance(sourceAccountUser.getAccountBalance().subtract(transactionRequest.getAmount()));
            userRepository.save(sourceAccountUser);
        }
        Transaction debitTransaction = mapper.mapToEntity(transactionRequest, "DEBIT", sourceAccountUser.getAccountNumber());
        transactionRepository.save(debitTransaction);
        mailService.sendEmailAlert(EmailDetails.builder()
                .recipient(sourceAccountUser.getEmail())
                .subject("DEBIT ALERT")
                .messageBody("The sum of " + transactionRequest.getAmount() + " has been deducted from your account!\n" +
                        "Your current balance is " + sourceAccountUser.getAccountBalance())
                .build());

        User destinationAccountUser = userRepository.findByAccountNumber(transactionRequest.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("User", "account number", transactionRequest.getAccountNumber()));
        destinationAccountUser.setAccountBalance(destinationAccountUser.getAccountBalance().add(transactionRequest.getAmount()));
        userRepository.save(destinationAccountUser);

        Transaction creditTransaction = mapper.mapToEntity(transactionRequest, "CREDIT", destinationAccountUser.getAccountNumber());
        transactionRepository.save(creditTransaction);
        mailService.sendEmailAlert(EmailDetails.builder()
                .recipient(sourceAccountUser.getEmail())
                .subject("CREDIT ALERT")
                .messageBody("The sum of " + transactionRequest.getAmount() + " has been sent to your account from " +
                        sourceAccountUser.getFirstName() + " " + sourceAccountUser.getLastName() + "\n" +
                        "Your current balance is " + sourceAccountUser.getAccountBalance())
                .build());
        return mapper.mapToDto(debitTransaction, destinationAccountUser.getAccountNumber());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "transactions", key = "'user' + {#pageNumber, #pageSize, #sortBy, #sortDir}")
    public List<TransactionResponse> getTransaction(Integer pageNumber, Integer pageSize, String sortBy, String sortDir) {
        Sort sort = (sortDir.equalsIgnoreCase("asc"))
                ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable p = PageRequest.of(pageNumber, pageSize, sort);
        User user = authService.getCurrentUser();
        Page<Transaction> transactionPage = transactionRepository.findByAccountNumber(user.getAccountNumber(), p);
        List<Transaction> transactions = transactionPage.getContent();
        return transactions.stream().map(mapper::mapToDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "transactions", key = "{#pageNumber, #pageSize, #sortBy, #sortDir}")
    public List<TransactionResponse> getAllTransaction(Integer pageNumber, Integer pageSize, String sortBy, String sortDir) {

        Sort sort = (sortDir.equalsIgnoreCase("asc"))
                ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable p = PageRequest.of(pageNumber, pageSize, sort);
        Page<Transaction> transactionPage = transactionRepository.findAll(p);
        List<Transaction> transactions = transactionPage.getContent();
        return transactions.stream().map(mapper::mapToDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "transactions", key = "#id")
    public TransactionResponse getTransactionById(String id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "transaction ID", id));
        return mapper.mapToDto(transaction);
    }

    @Override
    @CacheEvict(value = "transactions", allEntries = true)
    public String clearCache() {
        return "Cache has been cleared";
    }

}
