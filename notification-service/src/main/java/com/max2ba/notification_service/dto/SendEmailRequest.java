package com.max2ba.notification_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SendEmailRequest(
        @NotNull
        UserOperation userOperation,

        @NotBlank
        @Email
        String email) {
}
