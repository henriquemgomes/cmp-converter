package com.henriquemgomes.cmpconverter.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AttatchPKIMessageSignatureDto {
    
    @JsonProperty("pki_message")
    private String pkiMessage;
    
    private String signature;

    public AttatchPKIMessageSignatureDto() {
    }

    public String getPkiMessage() {
        return this.pkiMessage;
    }

    public void setPkiMessage(String pkiMessage) {
        this.pkiMessage = pkiMessage;
    }

    public String getSignature() {
        return this.signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
    
}
