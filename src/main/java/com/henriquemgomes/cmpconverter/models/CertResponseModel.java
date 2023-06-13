package com.henriquemgomes.cmpconverter.models;

import java.io.IOException;
import java.math.BigInteger;

import org.bouncycastle.asn1.cmp.CertResponse;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CertResponseModel {

    @JsonProperty("cert_req_id")
    private BigInteger certReqId;

    @JsonProperty("pki_status_info")
    private PKIStatusInfoModel pkiStatusInfo;

    @JsonProperty("certified_key_pair")
    private CertifiedKeyPairModel certifiedKeyPair;

    @JsonProperty("rsp_info")
    private String rspInfo;

    public CertResponseModel() {
    }

    public CertResponseModel(CertResponse certResponse) throws IOException {
        if (certResponse.getCertReqId() != null) 
            this.certReqId = certResponse.getCertReqId().getPositiveValue();
        
        if(certResponse.getStatus() != null)
            this.pkiStatusInfo = new PKIStatusInfoModel(certResponse.getStatus());

        if(certResponse.getCertifiedKeyPair() != null)
            this.certifiedKeyPair = new CertifiedKeyPairModel(certResponse.getCertifiedKeyPair());

    }

    public BigInteger getCertReqId() {
        return this.certReqId;
    }

    public void setCertReqId(BigInteger certReqId) {
        this.certReqId = certReqId;
    }

    public PKIStatusInfoModel getPkiStatusInfo() {
        return this.pkiStatusInfo;
    }

    public void setPkiStatusInfo(PKIStatusInfoModel pkiStatusInfo) {
        this.pkiStatusInfo = pkiStatusInfo;
    }

    public CertifiedKeyPairModel getCertifiedKeyPair() {
        return this.certifiedKeyPair;
    }

    public void setCertifiedKeyPair(CertifiedKeyPairModel certifiedKeyPair) {
        this.certifiedKeyPair = certifiedKeyPair;
    }

    public String getRspInfo() {
        return this.rspInfo;
    }

    public void setRspInfo(String rspInfo) {
        this.rspInfo = rspInfo;
    }
    
}
