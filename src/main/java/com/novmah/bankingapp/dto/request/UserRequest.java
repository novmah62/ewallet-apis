package com.novmah.bankingapp.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {

    @NotNull(message = "First name cannot be null")
    @NotEmpty(message = "First name cannot be empty")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "First name must contain only alphabetic characters")
    private String firstName;

    @NotNull(message = "Last name cannot be null")
    @NotEmpty(message = "Last name cannot be empty")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Last name must contain only alphabetic characters")
    private String lastName;

    @NotNull(message = "Gender cannot be null")
    @NotEmpty(message = "Gender cannot be empty")
    @Pattern(regexp = "^[M|F]$", message = "Gender must be either M or F")
    private String gender;

    @NotNull(message = "Address cannot be null")
    @NotEmpty(message = "Address cannot be empty")
    @Pattern(regexp = "^.+$", message = "Address must not be empty")
    private String address;

    @NotNull(message = "State of origin cannot be null")
    @NotEmpty(message = "State of origin cannot be empty")
    @Pattern(regexp = "^[A-Z]{2}$", message = "State of origin must be a two-letter abbreviation")
    private String stateOfOrigin;

    @NotNull(message = "Email address cannot be null")
    @NotEmpty(message = "Email address cannot be empty")
    @Email(message = "Email address is not valid")
    private String email;

    @NotNull(message = "Phone number cannot be null")
    @NotEmpty(message = "Phone number cannot be empty")
    @Pattern(regexp = "^[0-9]{10,12}$", message = "Phone number must be between 10 and 12 digits long")
    private String phoneNumber;

    @NotNull(message = "Phone number cannot be null")
    @NotEmpty(message = "Phone number cannot be empty")
    @Pattern(regexp = "^[0-9]{10,12}$", message = "Phone number must be between 10 and 12 digits long")
    private String alternativePhoneNumber;

}
