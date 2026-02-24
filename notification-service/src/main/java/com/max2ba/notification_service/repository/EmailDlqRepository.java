package com.max2ba.notification_service.repository;

import com.max2ba.notification_service.entity.EmailDlq;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public interface EmailDlqRepository extends JpaRepository<EmailDlq, UUID> {
     void deleteByCreatedAtBefore(LocalDateTime cutoff);
}
