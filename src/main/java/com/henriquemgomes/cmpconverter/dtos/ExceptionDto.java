package com.henriquemgomes.cmpconverter.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;

@Data
@JsonPropertyOrder({"error_code", "message", "error_id", "trace"})
public class ExceptionDto {
    @JsonProperty("error_code")
    String errorCode;

    String message;
    
    @JsonProperty("error_id")
    int errorId;

    String trace;

    public ExceptionDto(String errorCode, String message, int errorId) {
        this.errorCode = errorCode;
        this.message = message;
        this.errorId = errorId;
    }

}
