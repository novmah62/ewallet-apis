package com.novmah.bankingapp.service.impl;

import com.novmah.bankingapp.dto.request.TransactionRequest;
import com.novmah.bankingapp.dto.response.*;
import com.novmah.bankingapp.entity.Account;
import com.novmah.bankingapp.entity.Transaction;
import com.novmah.bankingapp.entity.User;
import com.novmah.bankingapp.exception.ResourceNotFoundException;
import com.novmah.bankingapp.mapper.TransactionMapper;
import com.novmah.bankingapp.repository.AccountRepository;
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
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final TransactionMapper mapper;
    private final AuthService authService;
    private final MailService mailService;

    @Override
    @CacheEvict(value = "transactions", allEntries = true)
    public BankResponse credit(TransactionRequest transactionRequest) {
        boolean isAccountExist = accountRepository.existsByAccountNumber(transactionRequest.getAccountNumber());
        if (!isAccountExist) {
            throw new ResourceNotFoundException(BankUtils.ACCOUNT_NOT_EXIST_MESSAGE);
        }

        Account account = accountRepository.findByAccountNumber(transactionRequest.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "account number", transactionRequest.getAccountNumber()));
        account.setAccountBalance(account.getAccountBalance().add(transactionRequest.getAmount()));
        accountRepository.save(account);

        Transaction transaction = mapper.mapToEntity(transactionRequest, "CREDIT", account.getAccountNumber());
        transactionRepository.save(transaction);

        User user = userRepository.findByAccount_AccountNumber(transactionRequest.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("User", "account number", transactionRequest.getAccountNumber()));
        return BankResponse.builder()
                .responseStatus("SUCCESS")
                .responseMessage(BankUtils.ACCOUNT_CREDITED_SUCCESS_MESSAGE)
                .userName(user.getFirstName() + " " + user.getLastName())
                .accountInfo(AccountInfo.builder()
                        .accountNumber(account.getAccountNumber())
                        .accountBalance(account.getAccountBalance())
                        .status(account.getStatus()).build()).build();
    }

    @Override
    @CacheEvict(value = "transactions", allEntries = true)
    public BankResponse debit(TransactionRequest transactionRequest) {
        boolean isAccountExist = accountRepository.existsByAccountNumber(transactionRequest.getAccountNumber());
        if (!isAccountExist) {
            throw new ResourceNotFoundException(BankUtils.ACCOUNT_NOT_EXIST_MESSAGE);
        }

        Account account = accountRepository.findByAccountNumber(transactionRequest.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("User", "account number", transactionRequest.getAccountNumber()));
        if (transactionRequest.getAmount().compareTo(account.getAccountBalance()) > 0) {
            throw new ResourceNotFoundException(BankUtils.INSUFFICIENT_BALANCE_MESSAGE);
        } else {
            account.setAccountBalance(account.getAccountBalance().subtract(transactionRequest.getAmount()));
            accountRepository.save(account);
        }

        Transaction transaction = mapper.mapToEntity(transactionRequest, "DEBIT", account.getAccountNumber());
        transactionRepository.save(transaction);

        User user = userRepository.findByAccount_AccountNumber(transactionRequest.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("User", "account number", transactionRequest.getAccountNumber()));
        return BankResponse.builder()
                .responseStatus("SUCCESS")
                .responseMessage(BankUtils.ACCOUNT_DEBITED_SUCCESS_MESSAGE)
                .userName(user.getFirstName() + " " + user.getLastName())
                .accountInfo(AccountInfo.builder()
                        .accountNumber(account.getAccountNumber())
                        .accountBalance(account.getAccountBalance())
                        .status(account.getStatus()).build()).build();
    }

    @Override
    @CacheEvict(value = "transactions", allEntries = true)
    public TransferResponse transfer(TransactionRequest transactionRequest) {
        boolean isDestinationAccountExist = accountRepository.existsByAccountNumber(transactionRequest.getAccountNumber());
        if (!isDestinationAccountExist) {
            throw new ResourceNotFoundException(BankUtils.ACCOUNT_NOT_EXIST_MESSAGE);
        }

        Account sourceAccount = authService.getCurrentAccount();
        if (transactionRequest.getAmount().compareTo(sourceAccount.getAccountBalance()) > 0) {
            throw new ResourceNotFoundException(BankUtils.INSUFFICIENT_BALANCE_MESSAGE);
        } else {
            sourceAccount.setAccountBalance(sourceAccount.getAccountBalance().subtract(transactionRequest.getAmount()));
            accountRepository.save(sourceAccount);
        }
        Transaction debitTransaction = mapper.mapToEntity(transactionRequest, "DEBIT", sourceAccount.getAccountNumber());
        transactionRepository.save(debitTransaction);
        User sourceUser = userRepository.findByAccount_AccountNumber(sourceAccount.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("User", "account number", transactionRequest.getAccountNumber()));
        mailService.sendEmailAlert(EmailDetails.builder()
                .recipient(sourceUser.getEmail())
                .subject("DEBIT ALERT")
                .messageBody("The sum of " + transactionRequest.getAmount() + " has been deducted from your account!\n" +
                        "Your current balance is " + sourceAccount.getAccountBalance())
                .build());

        Account destinationAccount = accountRepository.findByAccountNumber(transactionRequest.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("User", "account number", transactionRequest.getAccountNumber()));
        destinationAccount.setAccountBalance(destinationAccount.getAccountBalance().add(transactionRequest.getAmount()));
        accountRepository.save(destinationAccount);

        Transaction creditTransaction = mapper.mapToEntity(transactionRequest, "CREDIT", destinationAccount.getAccountNumber());
        transactionRepository.save(creditTransaction);
        User destinationUser = userRepository.findByAccount_AccountNumber(destinationAccount.getAccountNumber())
                        .orElseThrow(() -> new ResourceNotFoundException("User", "account number", transactionRequest.getAccountNumber()));
        mailService.sendEmailAlert(EmailDetails.builder()
                .recipient(destinationUser.getEmail())
                .subject("CREDIT ALERT")
                .messageBody("The sum of " + transactionRequest.getAmount() + " has been sent to your account from " +
                        destinationUser.getFirstName() + " " + destinationUser.getLastName() + "\n" +
                        "Your current balance is " + destinationAccount.getAccountBalance())
                .build());
        return mapper.mapToDto(debitTransaction, destinationAccount.getAccountNumber());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "transactions", key = "'account' + {#pageNumber, #pageSize, #sortBy, #sortDir}")
    public List<TransactionResponse> getTransaction(Integer pageNumber, Integer pageSize, String sortBy, String sortDir) {
        Sort sort = (sortDir.equalsIgnoreCase("asc"))
                ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable p = PageRequest.of(pageNumber, pageSize, sort);
        Account account = authService.getCurrentAccount();
        Page<Transaction> transactionPage = transactionRepository.findByAccountNumber(account.getAccountNumber(), p);
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
