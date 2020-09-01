package com.cdhi.projectivbackend.config;

import com.cdhi.projectivbackend.services.EmailService;
import com.cdhi.projectivbackend.services.SmtpEmailService;
import com.cdhi.projectivbackend.services.jobs.ConfirmEmailTimeOut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@Profile("dev")
@EnableScheduling
public class DevConfig {

    private static final int interval = 300000;

    @Autowired
    private ConfirmEmailTimeOut confirmEmailTimeOut;

    @Bean
    public EmailService emailService() {
        return new SmtpEmailService();
    }

    @Scheduled(fixedRate = interval)
    public void scheduleFixedRateTask() {
        confirmEmailTimeOut.checkout(interval);
    }
}
