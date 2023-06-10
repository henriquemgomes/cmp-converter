package com.henriquemgomes.cmpconverter.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SignatureDto {
    @JsonProperty("pki_message")
    private String pkiMessage;
    
    @JsonProperty("priv_key")
    private String privKey;

    public SignatureDto() {
    }

    public String getPkiMessage() {
        return this.pkiMessage;
    }

    public void setPkiMessage(String pkiMessage) {
        this.pkiMessage = pkiMessage;
    }

    public String getPrivKey() {
        return this.privKey;
    }

    public void setPrivKey(String privKey) {
        this.privKey = privKey;
    }

}
