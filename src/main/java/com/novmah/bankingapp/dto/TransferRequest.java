package com.novmah.bankingapp.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequest {

    @NotNull(message = "Destination account number cannot be null")
    @NotEmpty(message = "Destination account number cannot be empty")
    @Pattern(regexp = "^[0-9]+$", message = "Destination account number must contain only numeric characters")
    private String destinationAccountNumber;

    @NotNull(message = "Amount cannot be null")
    @PositiveOrZero(message = "Amount must be positive or zero")
    private BigDecimal amount;

}
