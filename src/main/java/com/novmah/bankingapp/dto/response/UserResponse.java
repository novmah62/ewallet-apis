package com.novmah.bankingapp.dto.response;

import com.novmah.bankingapp.entity.Role;
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
public class UserResponse implements Serializable {

    private String fullName;

    private String gender;

    private String address;

    private String stateOfOrigin;

//    private String accountNumber;

//    private BigDecimal accountBalance;

    private String email;

    private String phoneNumber;

    private String alternativePhoneNumber;

//    private String status;

//    private Role role;

//    private String createdAt;

//    private String modifiedAt;
}
