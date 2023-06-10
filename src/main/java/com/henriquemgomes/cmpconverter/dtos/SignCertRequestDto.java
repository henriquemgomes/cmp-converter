package com.henriquemgomes.cmpconverter.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SignCertRequestDto {
    @JsonProperty("cert_request")
    private String certRequest;
    
    @JsonProperty("priv_key")
    private String privKey;

    public SignCertRequestDto() {
    }

    public String getCertRequest() {
        return this.certRequest;
    }

    public void setCertRequest(String certRequest) {
        this.certRequest = certRequest;
    }

    public String getPrivKey() {
        return this.privKey;
    }

    public void setPrivKey(String privKey) {
        this.privKey = privKey;
    }

}
