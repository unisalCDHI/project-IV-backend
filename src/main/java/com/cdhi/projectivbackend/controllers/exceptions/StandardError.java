package com.cdhi.projectivbackend.controllers.exceptions;

import java.io.Serializable;
import java.util.List;

public class StandardError implements Serializable {
    private Long timeStamp;
    private Integer status;
    private String error;
    private List<Error> errors;

    public StandardError(Integer status, List<Error> errors, String error, Long timeStamp) {
        this.status = status;
        this.error = error;
        this.timeStamp = timeStamp;
        this.errors = errors;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public List<Error> getErrors() {
        return errors;
    }

    public void setErrors(List<Error> errors) {
        this.errors = errors;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
