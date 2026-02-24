package com.max2ba.notification_service.entity;

import com.max2ba.notification_service.dto.UserOperation;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "mail_dlq")
public class EmailDlq {
     @Id
     @GeneratedValue
     private UUID id;

     @Column(nullable = false)
     private String email;

     @Column(nullable = false)
     private String subject;

     @Column(nullable = false)
     private String text;

     @Enumerated(EnumType.STRING)
     private UserOperation userOperation;

     private Integer attempts = 0;

     @CreationTimestamp
     @Column(name = "created_at", updatable = false, nullable = false)
     private LocalDateTime createdAt;
}
