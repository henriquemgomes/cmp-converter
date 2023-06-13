package com.henriquemgomes.cmpconverter.models;

import java.io.IOException;
import org.bouncycastle.asn1.crmf.CertReqMsg;

import com.fasterxml.jackson.annotation.JsonProperty;



 /*
    CertReqMsg ::= SEQUENCE {
        certReq   CertRequest,
        popo       ProofOfPossession  OPTIONAL,
        -- content depends upon key type
        regInfo   SEQUENCE SIZE(1..MAX) OF AttributeTypeAndValue OPTIONAL 
    }
 */

public class CertReqMsgModel {

    @JsonProperty("cert_req")
    private CertRequestModel certReq;

    private ProofOfPossessionModel popo;

    @JsonProperty("reg_info")
    private RegInfoModel regInfo;

    public CertReqMsgModel() {
    }

    public CertReqMsgModel(CertReqMsg certReqMsg) throws IOException {
        CertRequestModel certRequestModel = new CertRequestModel(certReqMsg.getCertReq());
        this.setCertReq(certRequestModel);
        RegInfoModel regInfoModel = new RegInfoModel(certReqMsg.getRegInfo());
        this.setRegInfo(regInfoModel);
    }

    public CertReqMsgModel(CertRequestModel certReq) {
        this.certReq = certReq;
    }

    public CertReqMsgModel(CertRequestModel certReq, RegInfoModel regInfo) {
        this.certReq = certReq;
        this.regInfo = regInfo;
    }

    public CertRequestModel getCertReq() {
        return this.certReq;
    }

    public void setCertReq(CertRequestModel certReq) {
        this.certReq = certReq;
    }

    public RegInfoModel getRegInfo() {
        return this.regInfo;
    }

    public void setRegInfo(RegInfoModel regInfo) {
        this.regInfo = regInfo;
    }

    public ProofOfPossessionModel getPopo() {
        return this.popo;
    }

    public void setPopo(ProofOfPossessionModel popo) {
        this.popo = popo;
    }


}
