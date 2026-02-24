package com.max2ba.notification_service.service;

import com.max2ba.notification_service.dto.SendEmailRequest;

public interface EmailService {
     void sendEmail(SendEmailRequest request);
}

