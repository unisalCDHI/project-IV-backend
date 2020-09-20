package com.cdhi.projectivbackend.controllers.exceptions;

public class Error {
    private String defaultMessage;

    public Error(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    public void setDefaultMessage(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }
}
