package com.max2ba.notification_service.dto;

public record ApiResponse(
        ResponseCode code,
        String message,
        String details
) {
     public static ApiResponse success(String details) {
          return new ApiResponse(ResponseCode.SUCCESS, ResponseCode.SUCCESS.message(), details);
     }

     public static ApiResponse error(ResponseCode code, String details) {
          return new ApiResponse(code, code.message(), details);
     }
}

