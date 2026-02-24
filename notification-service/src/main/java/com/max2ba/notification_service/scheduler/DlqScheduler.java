package com.max2ba.notification_service.scheduler;

import com.max2ba.notification_service.annotation.Loggable;
import com.max2ba.notification_service.repository.EmailDlqRepository;
import com.max2ba.notification_service.service.EmailDlqService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Loggable
public class DlqScheduler {

     private final EmailDlqService emailDlqService;
     private final EmailDlqRepository emailDlqRepository;

     @Scheduled(cron = "0 0 0 * * *")
     public void retryDlqEmails() {
          emailDlqService.processDlq();
     }

     @Scheduled(cron = "0 0 1 * * *")
     public void cleanupOldRecords() {
          LocalDateTime cutoff = LocalDateTime.now().minusWeeks(1);
          emailDlqRepository.deleteByCreatedAtBefore(cutoff);
     }
}