package com.max2ba.notification_service.service;

import com.max2ba.notification_service.annotation.Loggable;
import com.max2ba.notification_service.dto.SendEmailRequest;
import com.max2ba.notification_service.exception.EmailSendException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Loggable
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
     private static final String SUBJECT_CREATE = "Ваш аккаунт успешно создан";
     private static final String SUBJECT_DELETE = "Ваш аккаунт был удалён";
     private static final String TEXT_CREATE = "Здравствуйте! Ваш аккаунт был успешно создан.";
     private static final String TEXT_DELETE = "Здравствуйте! Ваш аккаунт был удалён.";

     @Value("${app.from}")
     private String from;
     private final JavaMailSender mailSender;

     @Override
     public void sendEmail(SendEmailRequest request) {
          switch (request.userOperation()) {
               case CREATE -> send(request.email(), SUBJECT_CREATE, TEXT_CREATE);
               case DELETE -> send(request.email(), SUBJECT_DELETE, TEXT_DELETE);
          }
     }

     private void send(String to, String subject, String text) {
          try {
               SimpleMailMessage message = new SimpleMailMessage();
               message.setFrom(from);
               message.setTo(to);
               message.setSubject(subject);
               message.setText(text);
               mailSender.send(message);
          } catch (MailException e) {
               log.error("Mail exception: {}, {}", e.getMessage(), e.getStackTrace());
               throw new EmailSendException("Ошибка отправки письма", e);
          }
     }
}