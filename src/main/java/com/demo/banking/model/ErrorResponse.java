package com.demo.banking.model;

/**
 * Simple error response model for REST API error handling
 */
public class ErrorResponse {
    private String message;

    public ErrorResponse() {
    }

    public ErrorResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
