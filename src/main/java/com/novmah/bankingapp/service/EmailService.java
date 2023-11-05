package com.novmah.bankingapp.service;

import com.novmah.bankingapp.dto.EmailDetails;

public interface EmailService {

    void sendEmailAlert(EmailDetails emailDetails);
    void sendEmailWithAttachment(EmailDetails emailDetails);

}
