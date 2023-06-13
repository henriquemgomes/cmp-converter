package com.henriquemgomes.cmpconverter.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VerifySignatureDto {
    
    @JsonProperty("protected_pki_message")
    private String pkiMessage;
    
    @JsonProperty("public_key")
    private String publicKey;

    public VerifySignatureDto() {
    }

    public String getPkiMessage() {
        return this.pkiMessage;
    }

    public void setPkiMessage(String pkiMessage) {
        this.pkiMessage = pkiMessage;
    }

    public String getPublicKey() {
        return this.publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

}
