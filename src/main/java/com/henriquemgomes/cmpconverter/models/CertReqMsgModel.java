package com.henriquemgomes.cmpconverter.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;



 /*
    CertReqMsg ::= SEQUENCE {
        certReq   CertRequest,
        popo       ProofOfPossession  OPTIONAL,
        -- content depends upon key type
        regInfo   SEQUENCE SIZE(1..MAX) OF AttributeTypeAndValue OPTIONAL 
    }
 */

public class CertReqMsgModel {
    @NotNull(message = "body.cert_req_messages.Array<cert_req> is required")
    @JsonProperty("cert_req")
    @Valid
    private CertRequestModel certReq;
    //TODO private ProofOfPossession popo;

    @JsonProperty("reg_info")
    private List<@Valid AttributeTypeAndValueModel> regInfo;

    public CertReqMsgModel() {
    }

    public CertReqMsgModel(CertRequestModel certReq) {
        this.certReq = certReq;
    }

    public CertReqMsgModel(CertRequestModel certReq, List<AttributeTypeAndValueModel> regInfo) {
        this.certReq = certReq;
        this.regInfo = regInfo;
    }

    public CertRequestModel getCertReq() {
        return this.certReq;
    }

    public void setCertReq(CertRequestModel certReq) {
        this.certReq = certReq;
    }

    public List<AttributeTypeAndValueModel> getRegInfo() {
        return this.regInfo;
    }

    public void setRegInfo(List<AttributeTypeAndValueModel> regInfo) {
        this.regInfo = regInfo;
    }


}
