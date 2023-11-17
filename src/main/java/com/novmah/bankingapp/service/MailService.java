package com.novmah.bankingapp.service;

import com.novmah.bankingapp.dto.response.EmailDetails;

public interface MailService {

    void sendEmailAlert(EmailDetails emailDetails);
    void sendEmailWithAttachment(EmailDetails emailDetails);

}
