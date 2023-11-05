package com.novmah.bankingapp.service;

import com.itextpdf.text.DocumentException;
import com.novmah.bankingapp.entity.Transaction;
import com.novmah.bankingapp.entity.User;

import java.io.FileNotFoundException;
import java.util.List;

public interface StatementService {

    List<Transaction> generateStatement(String accountNumber, String startDate, String endDate) throws DocumentException, FileNotFoundException;

}
