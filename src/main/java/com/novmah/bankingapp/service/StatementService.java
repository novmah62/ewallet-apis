package com.novmah.bankingapp.service;

import com.itextpdf.text.DocumentException;
import com.novmah.bankingapp.entity.Transaction;

import java.io.FileNotFoundException;
import java.util.List;

public interface StatementService {

    List<Transaction> generateStatement(String startDate, String endDate) throws DocumentException, FileNotFoundException;

}
