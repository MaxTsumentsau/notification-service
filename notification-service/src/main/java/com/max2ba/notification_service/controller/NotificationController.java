package com.max2ba.notification_service.controller;

import com.max2ba.notification_service.annotation.Loggable;
import com.max2ba.notification_service.dto.ApiResponse;
import com.max2ba.notification_service.dto.SendEmailRequest;
import com.max2ba.notification_service.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Loggable
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

     private final EmailService emailService;

     @PostMapping("/send")
     public ResponseEntity<ApiResponse> sendEmail(@Valid @RequestBody SendEmailRequest request) {
          emailService.sendEmail(request);
          return ResponseEntity.ok(ApiResponse
                  .success(request.userOperation().name() + ": " + request.email()));
     }
}