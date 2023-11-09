package com.novmah.bankingapp.controller;

import com.itextpdf.text.DocumentException;
import com.novmah.bankingapp.dto.BankResponse;
import com.novmah.bankingapp.dto.CreditDebitRequest;
import com.novmah.bankingapp.dto.TransferRequest;
import com.novmah.bankingapp.entity.Transaction;
import com.novmah.bankingapp.service.StatementService;
import com.novmah.bankingapp.service.TransactionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/api/transaction")
@RequiredArgsConstructor
@Tag(name = "Transaction Management APIs")
public class TransactionController {

    private final TransactionService transactionService;
    private final StatementService statementService;

    @PostMapping("/credit")
    @ResponseStatus(HttpStatus.CREATED)
    public BankResponse credit(@Valid @RequestBody CreditDebitRequest request) {
        return transactionService.credit(request);
    }

    @PostMapping("/debit")
    @ResponseStatus(HttpStatus.CREATED)
    public BankResponse debit(@Valid @RequestBody CreditDebitRequest request) {
        return transactionService.debit(request);
    }

    @PostMapping("/transfer")
    @ResponseStatus(HttpStatus.CREATED)
    public BankResponse transfer(@Valid @RequestBody TransferRequest request) {
        return transactionService.transfer(request);
    }

    @GetMapping("/statement")
    @ResponseStatus(HttpStatus.OK)
    public List<Transaction> generateStatement(@RequestParam String accountNumber,
                                               @RequestParam String startDate,
                                               @RequestParam String endDate) throws DocumentException, FileNotFoundException {
        return statementService.generateStatement(accountNumber, startDate, endDate);
    }

}
