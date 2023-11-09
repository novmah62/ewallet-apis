package com.novmah.bankingapp.dto;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BankResponse {

    private String responseCode;

    private String responseMessage;

    private AccountInfo accountInfo;

}
