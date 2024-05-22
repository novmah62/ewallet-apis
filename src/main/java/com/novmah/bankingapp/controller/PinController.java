package com.novmah.bankingapp.controller;

import com.novmah.bankingapp.service.PinService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pin")
@RequiredArgsConstructor
public class PinController {

    private final PinService pinService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public String isPinCreated() {
        return pinService.isPinCreated();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createPin(String accountNumber, String password, String pin) {
        pinService.createPin(accountNumber, password, pin);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public void updatePin(String accountNumber, String oldPin, String password, String newPin) {
        pinService.updatePin(accountNumber, oldPin, password, newPin);
    }

}
