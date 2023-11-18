package com.novmah.bankingapp.controller;

import com.itextpdf.text.DocumentException;
import com.novmah.bankingapp.dto.request.TransactionRequest;
import com.novmah.bankingapp.dto.response.ApiResponse;
import com.novmah.bankingapp.dto.response.BankResponse;
import com.novmah.bankingapp.dto.response.TransactionResponse;
import com.novmah.bankingapp.dto.response.TransferResponse;
import com.novmah.bankingapp.entity.Transaction;
import com.novmah.bankingapp.service.StatementService;
import com.novmah.bankingapp.service.TransactionService;
import com.novmah.bankingapp.utils.BankUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public BankResponse credit(@Valid @RequestBody TransactionRequest request) {
        return transactionService.credit(request);
    }

    @PostMapping("/debit")
    @ResponseStatus(HttpStatus.CREATED)
    public BankResponse debit(@Valid @RequestBody TransactionRequest request) {
        return transactionService.debit(request);
    }

    @PostMapping("/transfer")
    @ResponseStatus(HttpStatus.CREATED)
    public TransferResponse transfer(@Valid @RequestBody TransactionRequest request) {
        return transactionService.transfer(request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TransactionResponse> getTransaction(
            @RequestParam(value = "pageNumber", defaultValue = BankUtils.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = BankUtils.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(value = "sortBy", defaultValue = BankUtils.SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = BankUtils.SORT_DIR, required = false) String sortDir) {
        return transactionService.getTransaction(pageNumber, pageSize, sortBy, sortDir);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TransactionResponse getTransactionById(@PathVariable String id) {
        return transactionService.getTransactionById(id);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<TransactionResponse> getAllTransaction(
            @RequestParam(value = "pageNumber", defaultValue = BankUtils.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = BankUtils.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(value = "sortBy", defaultValue = BankUtils.SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = BankUtils.SORT_DIR, required = false) String sortDir) {
        return transactionService.getAllTransaction(pageNumber, pageSize, sortBy, sortDir);
    }

    @GetMapping("/statement")
    @ResponseStatus(HttpStatus.OK)
    public List<Transaction> generateStatement(@RequestParam String startDate,
                                               @RequestParam String endDate) throws DocumentException, FileNotFoundException {
        return statementService.generateStatement(startDate, endDate);
    }

    @GetMapping("/clear_cache")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ApiResponse clearCache() {
        return new ApiResponse(true, transactionService.clearCache());
    }

}
