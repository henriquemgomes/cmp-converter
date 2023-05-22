package com.henriquemgomes.cmpconverter.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;

public class RegInfoModel {
 
    @JsonProperty("utf8_pairs")
    private List<@Valid UTF8PairsModel> utf8pairs;

    @JsonProperty("cert_req")
    @Valid
    private CertRequestModel certReq;

    public RegInfoModel() {
    }

    public RegInfoModel(List<UTF8PairsModel> utf8pairs, CertRequestModel certReq) {
        this.utf8pairs = utf8pairs;
        this.certReq = certReq;
    }

    public List<UTF8PairsModel> getUtf8pairs() {
        return this.utf8pairs;
    }

    public void setUtf8pairs(List<UTF8PairsModel> utf8pairs) {
        this.utf8pairs = utf8pairs;
    }

    public CertRequestModel getCertReq() {
        return this.certReq;
    }

    public void setCertReq(CertRequestModel certReq) {
        this.certReq = certReq;
    }

}
