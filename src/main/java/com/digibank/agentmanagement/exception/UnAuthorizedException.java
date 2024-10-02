package com.digibank.agentmanagement.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationServiceException;

public class UnAuthorizedException extends AuthenticationServiceException {

    public UnAuthorizedException(String message) {
        super(message);
    }

    public String getDefaultBodyAsString() {
        return UnAuthError
                .builder()
                .statusCode(HttpStatus.UNAUTHORIZED.name())
                .message("UnAuthorized Access")
                .timestamp(LocalDateTime.now())
                .build().toJsonString();
    }
    @Data
    @AllArgsConstructor
    @Builder
    private static class UnAuthError{
        private String statusCode;
        private String message;
        private String path;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyy HH:mm:ss")
        private LocalDateTime timestamp;

        public UnAuthError() {
            timestamp = LocalDateTime.now();
        }

        public UnAuthError(String statusCode, String message, String path) {
            this();
            this.statusCode = statusCode;
            this.message = message;
            this.path = path;
        }

        public String toJsonString() {
            return "{\n" +
                    "    \"statusCode\": \"" + statusCode + "\",\n" +
                    "    \"message\": \"" + message + "\",\n" +
                    "    \"timestamp\": \"" + timestamp + "\"\n" +
                    "}";
        }
    }
}
