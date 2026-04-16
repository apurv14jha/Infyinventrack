package com.infy.dto;

public class ErrorResponseDto {
    private int status;
    private String errorCode;
    private String message;

    public ErrorResponseDto(int status, String errorCode, String message) {
        this.status = status;
        this.errorCode = errorCode;
        this.message = message;
    }

    public int getStatus() { return status; }
    public String getErrorCode() { return errorCode; }
    public String getMessage() { return message; }
}
