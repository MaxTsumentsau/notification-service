--liquibase formatted sql

--changeset MaxTumensev:001-create-email-dlq
-- date: 2026-02-22
-- task: add DLQ
CREATE TABLE mail_dlq (
                          id UUID PRIMARY KEY,
                          email VARCHAR(255) NOT NULL,
                          subject VARCHAR(255) NOT NULL,
                          text TEXT NOT NULL,
                          user_operation VARCHAR(50),
                          attempts INT NOT NULL DEFAULT 0,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

