package com.max2ba.notification_service.service;

import com.max2ba.notification_service.annotation.Loggable;
import com.max2ba.notification_service.dto.SendEmailRequest;
import com.max2ba.notification_service.entity.EmailDlq;
import com.max2ba.notification_service.repository.EmailDlqRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Loggable
public class EmailDlqService {

     private final EmailDlqRepository repository;
     private final EmailService emailService;

     public void saveToDlq(EmailDlq emailDlq) {
          repository.save(emailDlq);
     }

     public void processDlq() {
          List<EmailDlq> list = repository.findAll();

          list.stream().filter(entry -> entry.getAttempts() < 5).forEach(entry -> {
               try {
                    emailService.sendEmail(new SendEmailRequest(entry.getUserOperation(), entry.getEmail()));
                    repository.delete(entry);
               } catch (Exception e) {
                    entry.setAttempts(entry.getAttempts() + 1);
                    repository.save(entry);
               }
          });
     }
}

