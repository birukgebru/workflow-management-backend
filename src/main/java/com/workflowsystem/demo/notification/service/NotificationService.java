package com.workflowsystem.demo.notification.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger logger =
            LoggerFactory.getLogger(NotificationService.class);

    @Async("notificationExecutor")
    public void sendEmail(String to, String subject, String message) {

        logger.info(
        """
        ==========================
        EMAIL NOTIFICATION
        To: {}
        Subject: {}
        Message: {}
        ==========================
        """,
        to,
        subject,
        message
        );

        //TODO: I will implement SMTP later 
    }
}