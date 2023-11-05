package com.novmah.bankingapp.utils;

import java.security.SecureRandom;
import java.time.Year;
import java.util.Random;

public class AccountUtils {

    public static final String ACCOUNT_EXISTS_CODE = "001";
    public static final String ACCOUNT_EXISTS_MESSAGE = "This user already has an account created!";
    public static final String ACCOUNT_CREATION_SUCCESS_CODE = "002";
    public static final String ACCOUNT_CREATION_SUCCESS_MESSAGE = "Account has been successfully created!";
    public static final String ACCOUNT_NOT_EXIST_CODE = "003";
    public static final String ACCOUNT_NOT_EXIST_MESSAGE = "User with the provided Account Number does not exist";
    public static final String ACCOUNT_FOUND_SUCCESS_CODE = "004";
    public static final String ACCOUNT_FOUND_SUCCESS_MESSAGE = "User Account Found";
    public static final String ACCOUNT_CREDITED_SUCCESS_CODE = "005";
    public static final String ACCOUNT_CREDITED_SUCCESS_MESSAGE = "User Account was credited successfully";
    public static final String INSUFFICIENT_BALANCE_CODE = "006";
    public static final String INSUFFICIENT_BALANCE_MESSAGE = "Insufficient Balance";
    public static final String ACCOUNT_DEBITED_SUCCESS_CODE = "007";
    public static final String ACCOUNT_DEBITED_SUCCESS_MESSAGE = "Account has been successfully debited";
    public static final String TRANSFER_SUCCESSFUL_CODE = "008";
    public static final String TRANSFER_SUCCESSFUL_MESSAGE = "Transfer successful";

    public static String generateAccountNumber() {
        Random random = new SecureRandom();
        int min = 100000;
        int max = 999999;
        int randNumber = random.nextInt(max - min + 1) + min;

        String year = String.valueOf(Year.now());
        String randomNumber = String.valueOf(randNumber);

        return year + randomNumber;
    }
}
