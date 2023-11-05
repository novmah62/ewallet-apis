package com.novmah.bankingapp.service;

import com.novmah.bankingapp.dto.BankResponse;
import com.novmah.bankingapp.dto.CreditDebitRequest;
import com.novmah.bankingapp.dto.TransferRequest;

public interface TransactionService {

    BankResponse credit(CreditDebitRequest creditDebitRequest);
    BankResponse debit(CreditDebitRequest creditDebitRequest);
    BankResponse transfer(TransferRequest transferRequest);

}
