package com.henriquemgomes.cmpconverter.exceptions;

import org.springframework.http.HttpStatus;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CmpConverterException extends Exception {

    String errorCode;
    String message;
    int internalErrorCode;
    HttpStatus statusCode = HttpStatus.UNPROCESSABLE_ENTITY;
    String[] logInfo = {};

    public CmpConverterException(String errorCode, String message, int internalErrorCode, HttpStatus statusCode, String[] logInfo) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
        this.internalErrorCode = internalErrorCode;
        this.statusCode = statusCode;
        this.logInfo = logInfo;
        this.logError();
    }

    private void logError() {
        log.error("["+ this.errorCode +"] "+ this.message);
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getInternalErrorCode() {
        return this.internalErrorCode;
    }

    public void setInternalErrorCode(int internalErrorCode) {
        this.internalErrorCode = internalErrorCode;
    }

    public HttpStatus getStatusCode() {
        return this.statusCode;
    }

    public void setStatusCode(HttpStatus statusCode) {
        this.statusCode = statusCode;
    }

    public String[] getLogInfo() {
        return this.logInfo;
    }

    public void setLogInfo(String[] logInfo) {
        this.logInfo = logInfo;
    }

    
}
