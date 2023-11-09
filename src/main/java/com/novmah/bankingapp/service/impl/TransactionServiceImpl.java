package com.novmah.bankingapp.service.impl;

import com.novmah.bankingapp.dto.*;
import com.novmah.bankingapp.entity.Transaction;
import com.novmah.bankingapp.entity.User;
import com.novmah.bankingapp.exception.ResourceNotFoundException;
import com.novmah.bankingapp.repository.TransactionRepository;
import com.novmah.bankingapp.repository.UserRepository;
import com.novmah.bankingapp.service.AuthService;
import com.novmah.bankingapp.service.MailService;
import com.novmah.bankingapp.service.TransactionService;
import com.novmah.bankingapp.utils.AccountUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final MailService mailService;

    @Override
    public BankResponse credit(CreditDebitRequest creditDebitRequest) {
        boolean isAccountExist = userRepository.existsByAccountNumber(creditDebitRequest.getAccountNumber());
        if (!isAccountExist) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null).build();
        }

        User user = userRepository.findByAccountNumber(creditDebitRequest.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("User", "account number", creditDebitRequest.getAccountNumber()));
        user.setAccountBalance(user.getAccountBalance().add(creditDebitRequest.getAmount()));
        userRepository.save(user);

        transactionRepository.save(Transaction.builder()
                .transactionType("CREDIT")
                .accountNumber(user.getAccountNumber())
                .amount(creditDebitRequest.getAmount())
                .status("SUCCESS").build());

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREDITED_SUCCESS_CODE)
                .responseMessage(AccountUtils.ACCOUNT_CREDITED_SUCCESS_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountNumber(user.getAccountNumber())
                        .accountName(user.getFirstName() + " " + user.getLastName())
                        .accountBalance(user.getAccountBalance()).build()).build();
    }

    @Override
    public BankResponse debit(CreditDebitRequest creditDebitRequest) {
        boolean isAccountExist = userRepository.existsByAccountNumber(creditDebitRequest.getAccountNumber());
        if (!isAccountExist) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null).build();
        }

        User user = userRepository.findByAccountNumber(creditDebitRequest.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("User", "account number", creditDebitRequest.getAccountNumber()));
        if (creditDebitRequest.getAmount().compareTo(user.getAccountBalance()) > 0) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null).build();
        } else {
            user.setAccountBalance(user.getAccountBalance().subtract(creditDebitRequest.getAmount()));
            userRepository.save(user);
        }

        transactionRepository.save(Transaction.builder()
                .transactionType("DEBIT")
                .accountNumber(user.getAccountNumber())
                .amount(creditDebitRequest.getAmount())
                .status("SUCCESS").build());

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_DEBITED_SUCCESS_CODE)
                .responseMessage(AccountUtils.ACCOUNT_DEBITED_SUCCESS_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountNumber(user.getAccountNumber())
                        .accountName(user.getFirstName() + " " + user.getLastName())
                        .accountBalance(user.getAccountBalance()).build()).build();
    }

    @Override
    public BankResponse transfer(TransferRequest transferRequest) {
        boolean isDestinationAccountExist = userRepository.existsByAccountNumber(transferRequest.getDestinationAccountNumber());
        if (!isDestinationAccountExist) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null).build();
        }

        User sourceAccountUser = authService.getCurrentUser();
        if (transferRequest.getAmount().compareTo(sourceAccountUser.getAccountBalance()) > 0) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null).build();
        } else {
            sourceAccountUser.setAccountBalance(sourceAccountUser.getAccountBalance().subtract(transferRequest.getAmount()));
            userRepository.save(sourceAccountUser);
        }
        transactionRepository.save(Transaction.builder()
                .transactionType("DEBIT")
                .accountNumber(sourceAccountUser.getAccountNumber())
                .amount(transferRequest.getAmount())
                .status("SUCCESS").build());
        mailService.sendEmailAlert(EmailDetails.builder()
                .recipient(sourceAccountUser.getEmail())
                .subject("DEBIT ALERT")
                .messageBody("The sum of " + transferRequest.getAmount() + " has been deducted from your account!\n" +
                        "Your current balance is " + sourceAccountUser.getAccountBalance())
                .build());

        User destinationAccountUser = userRepository.findByAccountNumber(transferRequest.getDestinationAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("User", "account number", transferRequest.getDestinationAccountNumber()));
        destinationAccountUser.setAccountBalance(destinationAccountUser.getAccountBalance().add(transferRequest.getAmount()));
        userRepository.save(destinationAccountUser);
        transactionRepository.save(Transaction.builder()
                .transactionType("CREDIT")
                .accountNumber(destinationAccountUser.getAccountNumber())
                .amount(transferRequest.getAmount())
                .status("SUCCESS").build());
        mailService.sendEmailAlert(EmailDetails.builder()
                .recipient(sourceAccountUser.getEmail())
                .subject("CREDIT ALERT")
                .messageBody("The sum of " + transferRequest.getAmount() + " has been sent to your account from " +
                        sourceAccountUser.getFirstName() + " " + sourceAccountUser.getLastName() + "\n" +
                        "Your current balance is " + sourceAccountUser.getAccountBalance())
                .build());
        return BankResponse.builder()
                .responseCode(AccountUtils.TRANSFER_SUCCESSFUL_CODE)
                .responseMessage(AccountUtils.TRANSFER_SUCCESSFUL_MESSAGE)
                .accountInfo(null)
                .build();
    }

}
