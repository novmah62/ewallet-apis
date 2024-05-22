package com.novmah.bankingapp.service;

public interface PinService {

    String isPinCreated();
    void createPin(String accountNumber, String password, String pin);
    void updatePin(String accountNumber, String oldPin, String password, String newPin);

}
