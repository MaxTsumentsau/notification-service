package com.max2ba.notification_service.controller;

import com.max2ba.notification_service.advice.GlobalExceptionHandler;
import com.max2ba.notification_service.dto.ApiResponse;
import com.max2ba.notification_service.dto.ResponseCode;
import com.max2ba.notification_service.dto.SendEmailRequest;
import com.max2ba.notification_service.dto.UserOperation;
import com.max2ba.notification_service.exception.EmailSendException;
import com.max2ba.notification_service.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mail.MailSendException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

     private RestTestClient client;

     @Mock
     private EmailService emailService;

     @InjectMocks
     private NotificationController controller;

     @BeforeEach
     void setUp() {
          //MockMvc с контроллером и контроллерЭдвайсом
          MockMvc mockMvc = MockMvcBuilders
                  .standaloneSetup(controller)
                  .setControllerAdvice(new GlobalExceptionHandler())
                  .build();

          //привязка RestTestClient к MockMvc
          client = RestTestClient.bindTo(mockMvc).build();
     }

     @Test
     void sendEmail_createOperation_success() {
          SendEmailRequest request = new SendEmailRequest(UserOperation.CREATE, "bambaleila@gmail.com");

          var response = client.post()
                  .uri("/api/notifications/send")
                  .body(request)
                  .exchange()
                  .expectStatus().isOk()
                  .expectBody(ApiResponse.class)
                  .returnResult()
                  .getResponseBody();

          verify(emailService).sendEmail(request);
          assertThat(response.code()).isEqualTo(ResponseCode.SUCCESS);
          assertThat(response.message()).isEqualTo(ResponseCode.SUCCESS.message());
          assertThat(response.details()).isEqualTo("bambaleila@gmail.com");
     }

     @Test
     void sendEmail_deleteOperation_success() {
          SendEmailRequest request = new SendEmailRequest(UserOperation.DELETE, "bambaleila@gmail.com");

          var response = client.post()
                  .uri("/api/notifications/send")
                  .body(request)
                  .exchange()
                  .expectStatus().isOk()
                  .expectBody(ApiResponse.class)
                  .returnResult()
                  .getResponseBody();

          verify(emailService).sendEmail(request);
          assertThat(response.code()).isEqualTo(ResponseCode.SUCCESS);
          assertThat(response.message()).isEqualTo(ResponseCode.SUCCESS.message());
          assertThat(response.details()).isEqualTo("bambaleila@gmail.com");
     }


     @Test
     void sendEmail_validationEmailError() {
          SendEmailRequest request = new SendEmailRequest(UserOperation.CREATE, "abrakadabra");

          var response = client.post()
                  .uri("/api/notifications/send")
                  .body(request)
                  .exchange()
                  .expectStatus().isBadRequest()
                  .expectBody(ApiResponse.class)
                  .returnResult()
                  .getResponseBody();

          verifyNoInteractions(emailService);
          assertThat(response.code()).isEqualTo(ResponseCode.VALIDATION_ERROR);
          assertThat(response.message()).isEqualTo(ResponseCode.VALIDATION_ERROR.message());
     }

     @Test
     void sendEmail_validationUserOperationError() {
          SendEmailRequest request = new SendEmailRequest(null, "bambaleila@gmail.com");

          var response = client.post()
                  .uri("/api/notifications/send")
                  .body(request)
                  .exchange()
                  .expectStatus().isBadRequest()
                  .expectBody(ApiResponse.class)
                  .returnResult()
                  .getResponseBody();

          verifyNoInteractions(emailService);
          assertThat(response.code()).isEqualTo(ResponseCode.VALIDATION_ERROR);
          assertThat(response.message()).isEqualTo(ResponseCode.VALIDATION_ERROR.message());
     }

     @Test
     void sendEmail_nullEmailError() {
          SendEmailRequest request = new SendEmailRequest(UserOperation.CREATE, null);

          var response = client.post()
                  .uri("/api/notifications/send")
                  .body(request)
                  .exchange()
                  .expectStatus().isBadRequest()
                  .expectBody(ApiResponse.class)
                  .returnResult()
                  .getResponseBody();

          verifyNoInteractions(emailService);
          assertThat(response.code()).isEqualTo(ResponseCode.VALIDATION_ERROR);
          assertThat(response.message()).isEqualTo(ResponseCode.VALIDATION_ERROR.message());
     }

     @Test
     void sendEmail_emptyBodyError() {
          var response = client.post()
                  .uri("/api/notifications/send")
                  .body("{}")
                  .exchange()
                  .expectStatus().is4xxClientError()
                  .expectBody(ApiResponse.class)
                  .returnResult()
                  .getResponseBody();

          verifyNoInteractions(emailService);
          assertThat(response.code()).isEqualTo(ResponseCode.UNSUPPORTED_MEDIA_TYPE);
          assertThat(response.message()).isEqualTo(ResponseCode.UNSUPPORTED_MEDIA_TYPE.message());
     }

     @Test
     void sendEmail_emailSendError() {
          SendEmailRequest request = new SendEmailRequest(UserOperation.CREATE, "bambaleila@gmail.com");

          doThrow(new EmailSendException("Ошибка отправки письма", new MailSendException("Mail exception")))
                  .when(emailService).sendEmail(request);

          var response = client.post()
                  .uri("/api/notifications/send")
                  .body(request)
                  .exchange()
                  .expectStatus().is5xxServerError()
                  .expectBody(ApiResponse.class)
                  .returnResult()
                  .getResponseBody();

          assertThat(response.code()).isEqualTo(ResponseCode.EMAIL_SEND_ERROR);
          assertThat(response.message()).isEqualTo(ResponseCode.EMAIL_SEND_ERROR.message());
          assertThat(response.details()).contains("Ошибка отправки письма");
     }

     @Test
     void sendEmail_invalidJsonSyntax() {
          var response = client.post()
                  .uri("/api/notifications/send")
                  .contentType(MediaType.APPLICATION_JSON)
                  .body("{ invalid json }")
                  .exchange()
                  .expectStatus().isBadRequest()
                  .expectBody(ApiResponse.class)
                  .returnResult()
                  .getResponseBody();

          assertThat(response.code()).isEqualTo(ResponseCode.INVALID_JSON);
     }

     @Test
     void sendEmail_unsupportedMediaType() {
          var response = client.post()
                  .uri("/api/notifications/send")
                  .contentType(MediaType.APPLICATION_XML)
                  .body("<xml></xml>")
                  .exchange()
                  .expectStatus().is4xxClientError()
                  .expectBody(ApiResponse.class)
                  .returnResult()
                  .getResponseBody();

          assertThat(response.code()).isEqualTo(ResponseCode.UNSUPPORTED_MEDIA_TYPE);
     }

     @Test
     void sendEmail_runtimeException() {
          SendEmailRequest request = new SendEmailRequest(UserOperation.CREATE, "bambaleila@gmail.com");

          doThrow(new RuntimeException("unchecked"))
                  .when(emailService).sendEmail(request);

          var response = client.post()
                  .uri("/api/notifications/send")
                  .body(request)
                  .exchange()
                  .expectStatus().is5xxServerError()
                  .expectBody(ApiResponse.class)
                  .returnResult()
                  .getResponseBody();

          assertThat(response.code()).isEqualTo(ResponseCode.INTERNAL_ERROR);
     }
}

