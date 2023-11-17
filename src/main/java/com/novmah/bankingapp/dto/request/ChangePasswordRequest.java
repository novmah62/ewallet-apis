package com.novmah.bankingapp.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ChangePasswordRequest {

    @NotNull(message = "Current password cannot be null")
    @NotEmpty(message = "Current password cannot be empty")
    @Size(min = 8, max = 20, message = "Current password must be between 8 and 20 characters long")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=])[a-zA-Z0-9@#$%^&+=]{8,20}$",
            message = "Password must contain at least one lowercase letter, one uppercase letter, " +
                    "one number, and one special character")
    private String currentPassword;

    @NotNull(message = "New password cannot be null")
    @NotEmpty(message = "New password cannot be empty")
    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters long")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=])[a-zA-Z0-9@#$%^&+=]{8,20}$",
            message = "New password must contain at least one lowercase letter, one uppercase letter, " +
                    "one number, and one special character")
    private String newPassword;

    @NotNull(message = "Confirmation password cannot be null")
    @NotEmpty(message = "Confirmation password cannot be empty")
    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters long")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=])[a-zA-Z0-9@#$%^&+=]{8,20}$",
            message = "Confirmation password must contain at least one lowercase letter, one uppercase letter, " +
                    "one number, and one special character")
    private String confirmationPassword;

}
