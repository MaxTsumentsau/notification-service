package com.max2ba.notification_service.dto;

public enum ResponseCode {
     SUCCESS("Сообщение успешно отправлено"),
     VALIDATION_ERROR("Некорректные данные запроса"),
     EMAIL_SEND_ERROR("Ошибка отправки письма"),
     NOT_FOUND("Ресурс не найден"),
     INVALID_JSON("Некорректный формат JSON"),
     UNSUPPORTED_MEDIA_TYPE("Неподдерживаемый Content-Type"),
     INTERNAL_ERROR("Внутренняя ошибка сервера"),
     UNEXPECTED_ERROR("Непредвиденная ошибка");

     private final String defaultMessage;

     ResponseCode(String defaultMessage) {
          this.defaultMessage = defaultMessage;
     }

     public String message() {
          return defaultMessage;
     }
}


