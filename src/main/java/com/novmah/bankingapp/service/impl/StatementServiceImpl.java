package com.novmah.bankingapp.service.impl;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.novmah.bankingapp.dto.response.EmailDetails;
import com.novmah.bankingapp.entity.Account;
import com.novmah.bankingapp.entity.Transaction;
import com.novmah.bankingapp.entity.User;
import com.novmah.bankingapp.exception.ResourceNotFoundException;
import com.novmah.bankingapp.repository.TransactionRepository;
import com.novmah.bankingapp.repository.UserRepository;
import com.novmah.bankingapp.service.AuthService;
import com.novmah.bankingapp.service.MailService;
import com.novmah.bankingapp.service.StatementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatementServiceImpl implements StatementService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final MailService mailService;

    private static final String FILE = "C:\\SharedStatements\\MyStatement.pdf";

    @Override
    public List<Transaction> generateStatement(String startDate, String endDate) throws DocumentException, FileNotFoundException {

        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);
        Account account = authService.getCurrentAccount();
        User user = userRepository.findByAccount_AccountNumber(account.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("User", "account number", account.getAccountNumber()));
        List<Transaction> transactions = transactionRepository.findByAccountNumber(account.getAccountNumber()).stream()
                .filter(transaction ->
                        transaction.getCreatedAt().isAfter(start.minusDays(1)) &&
                        transaction.getCreatedAt().isBefore(end.plusDays(1))).toList();
        designStatement(account, user, transactions, startDate, endDate);
        mailService.sendEmailWithAttachment(EmailDetails.builder()
                .recipient(user.getEmail())
                .subject("STATEMENT OF ACCOUNT")
                .messageBody("Kindly find your requested account statement attached!")
                .attachment(FILE)
                .build());
        return transactions;
    }

    public static void designStatement(Account account, User user, List<Transaction> transactions, String startDate, String endDate) throws FileNotFoundException, DocumentException {

        Rectangle statementSize = new Rectangle(PageSize.A4);
        Document document = new Document(statementSize);
        log.info("setting size of document");
        OutputStream outputStream = new FileOutputStream(FILE);
        PdfWriter.getInstance(document, outputStream);
        document.open();

        PdfPTable bankInfoTable = new PdfPTable(1);
        PdfPCell bankName = new PdfPCell(new Phrase("NOV_MAH BANK"));
        bankName.setBackgroundColor(BaseColor.GRAY);
        bankName.setPadding(20f);
        PdfPCell bankAddress = new PdfPCell(new Phrase("00-TamHung-ThanhOai-HaNoi"));

        bankInfoTable.addCell(bankName);
        bankInfoTable.addCell(bankAddress);

        PdfPTable statementInfo = new PdfPTable(2);
        PdfPCell statement = new PdfPCell(new Phrase("STATEMENT OF ACCOUNT"));
        statement.setBorder(0);
        PdfPCell start = new PdfPCell(new Phrase("Start Date: " + startDate));
        start.setBorder(0);
        PdfPCell end = new PdfPCell(new Phrase("End Date: " + endDate));
        end.setBorder(0);
        PdfPCell number = new PdfPCell(new Phrase("Account Number: " + account.getAccountNumber()));
        number.setBorder(0);
        PdfPCell name = new PdfPCell(new Phrase("Customer Name: " + user.getLastName() + " " + user.getFirstName()));
        name.setBorder(0);
        PdfPCell address = new PdfPCell(new Phrase("Customer Address: " + user.getAddress()));
        address.setBorder(0);
        PdfPCell space = new PdfPCell();
        space.setBorder(0);

        statementInfo.addCell(statement);
        statementInfo.addCell(start);
        statementInfo.addCell(number);
        statementInfo.addCell(end);
        statementInfo.addCell(name);
        statementInfo.addCell(space);
        statementInfo.addCell(address);
        statementInfo.addCell(space);

        PdfPTable transactionsTable = new PdfPTable(4);
        PdfPCell date = new PdfPCell(new Phrase("DATE"));
        date.setBackgroundColor(BaseColor.GRAY);
        date.setBorder(0);
        PdfPCell type = new PdfPCell(new Phrase("TYPE"));
        type.setBackgroundColor(BaseColor.GRAY);
        type.setBorder(0);
        PdfPCell amount = new PdfPCell(new Phrase("AMOUNT"));
        amount.setBackgroundColor(BaseColor.GRAY);
        amount.setBorder(0);
        PdfPCell message = new PdfPCell(new Phrase("MESSAGE"));
        message.setBackgroundColor(BaseColor.GRAY);
        message.setBorder(0);

        transactionsTable.addCell(date);
        transactionsTable.addCell(type);
        transactionsTable.addCell(amount);
        transactionsTable.addCell(message);

        transactions.forEach(transaction -> {
            transactionsTable.addCell(new Phrase(transaction.getCreatedAt().toString()));
            transactionsTable.addCell(new Phrase(transaction.getTransactionType()));
            transactionsTable.addCell(new Phrase(transaction.getAmount().toString()));
            transactionsTable.addCell(new Phrase(transaction.getMessage()));
        });

        document.add(bankInfoTable);
        document.add(statementInfo);
        document.add(transactionsTable);

        document.close();
    }

}
