package com.novmah.bankingapp.service;

import com.novmah.bankingapp.dto.request.TransactionRequest;
import com.novmah.bankingapp.dto.response.BankResponse;
import com.novmah.bankingapp.dto.response.TransactionResponse;
import com.novmah.bankingapp.dto.response.TransferResponse;

import java.util.List;

public interface TransactionService {

    BankResponse credit(TransactionRequest transactionRequest);
    BankResponse debit(TransactionRequest transactionRequest);
    TransferResponse transfer(TransactionRequest transactionRequest);
    TransactionResponse getTransactionById(String id);
    List<TransactionResponse> getTransaction(Integer pageNumber, Integer pageSize, String sortBy, String sortDir);
    List<TransactionResponse> getAllTransaction(Integer pageNumber, Integer pageSize, String sortBy, String sortDir);

}
