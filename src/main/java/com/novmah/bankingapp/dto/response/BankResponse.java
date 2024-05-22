package com.novmah.bankingapp.dto.response;

import lombok.*;

import java.io.Serializable;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BankResponse implements Serializable {

    private String responseStatus;

    private String responseMessage;

    private String userName;

    private AccountInfo accountInfo;

}
