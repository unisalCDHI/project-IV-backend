package com.cdhi.projectivbackend.services;

import com.cdhi.projectivbackend.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Date;

public abstract class AbstractEmailService implements EmailService {

    @Value("${default.sender}")
    private String sender;

    @Value("${confirm.account.url}")
    private String confirm_url;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public void sendUserConfirmationEmail(User obj) {
        SimpleMailMessage sm = prepareSimpleMailMessageFromUser(obj);
        sendEmail(sm);
    }

    protected SimpleMailMessage prepareSimpleMailMessageFromUser(User obj) {
        SimpleMailMessage sm = new SimpleMailMessage();
        sm.setTo(obj.getEmail());
        sm.setFrom(sender);
        sm.setSubject("CDHI - Usuário Cadastrado");
        sm.setText(obj.toString());
        return sm;
    }

    protected String htmlFromTemplateUser(User obj) {
        Context context = new Context();
        context.setVariable("user", obj);
        context.setVariable("confirm_url", confirm_url);
        context.setVariable("logo", "logo");
        return templateEngine.process("userConfirmation", context);
    }

    @Override
    public void sendUserConfirmationHtmlEmail(User obj) {
        try {
            MimeMessage mm = prepareMimeMessageFromUser(obj);
            sendHtmlEmail(mm);
        } catch (MessagingException e) {
            sendUserConfirmationEmail(obj);
        }
    }

    protected MimeMessage prepareMimeMessageFromUser(User obj) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mmh = new MimeMessageHelper(mimeMessage, true,"UTF-8");
        mmh.setTo(obj.getEmail());
        mmh.setFrom(sender);
        mmh.setSubject("CDHI - Usuário Cadastrado");
        mmh.setSentDate(new Date(System.currentTimeMillis()));
        mmh.setText(htmlFromTemplateUser(obj), true);
        mmh.addInline("logo", new ClassPathResource("static/img/logo.png"), "image/png");
        return mimeMessage;
    }

    @Override
    public void sendNewPasswordEmail(User user, String newPass) {
        SimpleMailMessage sm = prepareNewPasswordEmail(user, newPass);
        sendEmail(sm);
    }

    protected SimpleMailMessage prepareNewPasswordEmail(User user, String newPass) {
        SimpleMailMessage sm = new SimpleMailMessage();
        sm.setTo(user.getEmail());
        sm.setFrom(sender);
        sm.setSubject("CDHI - Solicitação de Nova Senha");
        sm.setText("Nova Senha: " + newPass);
        return sm;
    }

    @Override
    public void sendPasswordHtmlEmail(User obj, String newPass) {
        try {
            MimeMessage mm = prepareMimeMessageFromPassword(obj, newPass);
            sendHtmlEmail(mm);
        } catch (MessagingException e) {
            sendNewPasswordEmail(obj, newPass);
        }
    }

    protected MimeMessage prepareMimeMessageFromPassword(User obj, String newPass) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mmh = new MimeMessageHelper(mimeMessage, true,"UTF-8");
        mmh.setTo(obj.getEmail());
        mmh.setFrom(sender);
        mmh.setSubject("CDHI - Solicitação de Nova Senha");
        mmh.setSentDate(new Date(System.currentTimeMillis()));
        mmh.setText(htmlFromTemplatePassword(obj, newPass), true);
        mmh.addInline("logo", new ClassPathResource("static/img/logo.png"), "image/png");
        return mimeMessage;
    }

    protected String htmlFromTemplatePassword(User obj, String newPass) {
        Context context = new Context();
        context.setVariable("user", obj);
        context.setVariable("password", newPass);
        context.setVariable("logo", "logo");
        return templateEngine.process("password", context);
    }
}
