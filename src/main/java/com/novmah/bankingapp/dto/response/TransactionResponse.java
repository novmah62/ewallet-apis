package com.novmah.bankingapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse implements Serializable {

    private String transactionId;

    private String transactionType;

    private String accountNumber;

    private BigDecimal amount;

    private String status;

    private String message;

    private String createdAt;

    private String modifiedAt;

}
