package com.henriquemgomes.cmpconverter.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GenerateContentToSignDto {
    
    @JsonProperty("pki_message")
    private String pkiMessage;

    public GenerateContentToSignDto() {
    }

    public String getPkiMessage() {
        return this.pkiMessage;
    }

    public void setPkiMessage(String pkiMessage) {
        this.pkiMessage = pkiMessage;
    }

}
