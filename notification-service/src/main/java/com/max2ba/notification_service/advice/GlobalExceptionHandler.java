package com.max2ba.notification_service.advice;

import com.max2ba.notification_service.annotation.Loggable;
import com.max2ba.notification_service.dto.ApiResponse;
import com.max2ba.notification_service.dto.ResponseCode;
import com.max2ba.notification_service.exception.EmailSendException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;


@RestControllerAdvice
@Loggable
public class GlobalExceptionHandler {

     @ExceptionHandler(MethodArgumentNotValidException.class)
     public ResponseEntity<ApiResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
          String details = ex.getBindingResult().getFieldErrors().stream()
                  .map(err -> err.getField() + ": " + err.getDefaultMessage())
                  .reduce((a, b) -> a + "; " + b)
                  .orElse("Validation error");

          return ResponseEntity.badRequest().body(ApiResponse.error(ResponseCode.VALIDATION_ERROR, details));
     }

     @ExceptionHandler(EmailSendException.class)
     public ResponseEntity<ApiResponse> handleEmailError(EmailSendException ex) {
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                  .body(ApiResponse.error(ResponseCode.EMAIL_SEND_ERROR, ex.getMessage())
                  );
     }

     @ExceptionHandler(NoResourceFoundException.class)
     public ResponseEntity<ApiResponse> handleNotFound(NoResourceFoundException ex) {
          return ResponseEntity.status(HttpStatus.NOT_FOUND)
                  .body(ApiResponse.error(ResponseCode.NOT_FOUND, ex.getMessage())
                  );
     }

     @ExceptionHandler(HttpMessageNotReadableException.class)
     public ResponseEntity<ApiResponse> handleInvalidJson(HttpMessageNotReadableException ex) {
          return ResponseEntity.badRequest()
                  .body(ApiResponse.error(ResponseCode.INVALID_JSON, ex.getMessage())
                  );
     }

     @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
     public ResponseEntity<ApiResponse> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException ex) {
          return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                  .body(ApiResponse.error(ResponseCode.UNSUPPORTED_MEDIA_TYPE, ex.getMessage())
                  );
     }

     @ExceptionHandler(RuntimeException.class)
     public ResponseEntity<ApiResponse> handleRuntime(RuntimeException ex) {
          return ResponseEntity.internalServerError()
                  .body(ApiResponse.error(ResponseCode.INTERNAL_ERROR, ex.getMessage())
                  );
     }

     @ExceptionHandler(Exception.class)
     public ResponseEntity<ApiResponse> handleException(Exception ex) {
          return ResponseEntity.internalServerError()
                  .body(ApiResponse.error(ResponseCode.UNEXPECTED_ERROR, ex.getMessage())
                  );
     }
}

