package com.novmah.bankingapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransferResponse implements Serializable {

    private String status;

    private String message;

    private String transactionId;

    private String transactionType;

    private String sourceAccountNumber;

    private String destinationAccountNumber;

    private BigDecimal amount;

    private LocalDate createdAt;

    private LocalDate modifiedAt;

}
