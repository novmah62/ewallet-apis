package com.novmah.bankingapp.service.impl;

import com.novmah.bankingapp.dto.EmailDetails;
import com.novmah.bankingapp.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Async
    @Override
    public void sendEmailAlert(EmailDetails emailDetails) {
        MimeMessagePreparator message = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom(senderEmail);
            messageHelper.setTo(emailDetails.getRecipient());
            messageHelper.setSubject(emailDetails.getSubject());
            messageHelper.setText(emailDetails.getMessageBody());
        };
        try {
            mailSender.send(message);
            log.info("Mail send successfully");
        } catch (MailException e) {
            log.error("Failed to send mail: ", e);
            throw new RuntimeException();
        }
    }

    @Override
    public void sendEmailWithAttachment(EmailDetails emailDetails) {
        MimeMessagePreparator message = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
            FileSystemResource file = new FileSystemResource(new File(emailDetails.getAttachment()));
            messageHelper.setFrom(senderEmail);
            messageHelper.setTo(emailDetails.getRecipient());
            messageHelper.setSubject(emailDetails.getSubject());
            messageHelper.setText(emailDetails.getMessageBody());
            messageHelper.addAttachment(Objects.requireNonNull(file.getFilename()), file);
        };
        try {
            mailSender.send(message);
            log.info("Mail send successfully");
        } catch (MailException e) {
            log.error("Failed to send mail: ", e);
            throw new RuntimeException();
        }

    }
}
