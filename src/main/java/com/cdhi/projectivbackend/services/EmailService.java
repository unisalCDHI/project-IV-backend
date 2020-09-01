package com.cdhi.projectivbackend.services;

import com.cdhi.projectivbackend.domain.User;
import org.springframework.mail.SimpleMailMessage;

import javax.mail.internet.MimeMessage;

public interface EmailService {

    void sendUserConfirmationEmail(User obj);

    void sendEmail(SimpleMailMessage msg);

    void sendUserConfirmationHtmlEmail(User obj);

    void sendPasswordHtmlEmail(User obj, String newPass);

    void sendHtmlEmail(MimeMessage msg);

    void sendNewPasswordEmail(User user, String newPass);
}
