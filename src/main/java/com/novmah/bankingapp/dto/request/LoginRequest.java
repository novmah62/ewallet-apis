package com.novmah.bankingapp.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotNull(message = "Account number cannot be null")
    @NotEmpty(message = "Account number cannot be empty")
    @Pattern(regexp = "^[0-9]+$", message = "Account number must contain only numeric characters")
    private String accountNumber;

    @NotNull(message = "Password cannot be null")
    @NotEmpty(message = "Password cannot be empty")
    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters long")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=])[a-zA-Z0-9@#$%^&+=]{8,20}$",
            message = "Password must contain at least one lowercase letter, one uppercase letter, " +
                    "one number, and one special character")
    private String password;

}
