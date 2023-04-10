package com.henriquemgomes.cmpconverter.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;



public class CertificationRequestModel extends PKIBodyModel {

    @NotNull(message = "body.cert_req_messages is required")
    @NotEmpty(message = "body.cert_req_messages cannot be empty")
    @JsonProperty("cert_req_messages")
    private List<@Valid CertReqMsgModel> certReqMessages;

    public CertificationRequestModel() {
        super(PKIBodyModel.TYPE_CERT_REQ);
    }

    public CertificationRequestModel(List<CertReqMsgModel> certReqMessages) {
        super(PKIBodyModel.TYPE_CERT_REQ);
        this.certReqMessages = certReqMessages;
    }

    public List<CertReqMsgModel> getCertReqMessages() {
        return this.certReqMessages;
    }

    public void setCertReqMessages(List<CertReqMsgModel> certReqMessages) {
        this.certReqMessages = certReqMessages;
    }
    
}
