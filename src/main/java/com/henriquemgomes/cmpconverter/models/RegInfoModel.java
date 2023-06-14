package com.henriquemgomes.cmpconverter.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.crmf.AttributeTypeAndValue;
import org.bouncycastle.asn1.crmf.CRMFObjectIdentifiers;
import org.bouncycastle.asn1.crmf.CertRequest;
import org.bouncycastle.asn1.crmf.Controls;

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

    public RegInfoModel(AttributeTypeAndValue[] regInfo) throws IOException {
        if(regInfo != null) {
            List<UTF8PairsModel> utf8PairsList = new ArrayList<>();
                for (AttributeTypeAndValue attributeTypeAndValue : regInfo) {
                    if(attributeTypeAndValue.getType().toString().equals("1.3.6.1.5.5.7.7.5.2.1")){
                        ASN1TaggedObject taggedObject = DERTaggedObject.getInstance(attributeTypeAndValue.getValue());
                        utf8PairsList.add(new UTF8PairsModel(taggedObject.getBaseObject().toString()));
                    }
    
                    if(attributeTypeAndValue.getType().toString().equals("1.3.6.1.5.5.7.7.5.2.2")){
                        CertRequest certRequest = CertRequest.getInstance(attributeTypeAndValue.getValue());
                        CertRequestModel regInfoCertRequestModel = new CertRequestModel();
                        CertTemplateModel regInfoCertTemplateModel = new CertTemplateModel(certRequest.getCertTemplate());
    
                        regInfoCertRequestModel.setCertTemplate(regInfoCertTemplateModel);
    
                        Controls regInfoControls = certRequest.getControls();
        
                        ControlsModel regInfoControlsModel = new ControlsModel();
    
                        AttributeTypeAndValue[] regInfoControlsArray = regInfoControls.toAttributeTypeAndValueArray();
    
                        for (AttributeTypeAndValue regInfoControl : regInfoControlsArray) {
                            BasicControlModel basicControlModel = new BasicControlModel();
                            if(regInfoControl.getType().toString().equals(CRMFObjectIdentifiers.id_regCtrl_authenticator.toString())){
                                basicControlModel.setValue(regInfoControl.getValue().toString());
                                regInfoControlsModel.setAuthenticatorControl(basicControlModel);
                            }
    
                            if(regInfoControl.getType().equals(CRMFObjectIdentifiers.id_regCtrl_regToken)){
                                basicControlModel.setValue(regInfoControl.getValue().toString());
                                regInfoControlsModel.setRegTokenControl(basicControlModel);
                            }
                        }
    
                        regInfoCertRequestModel.setControls(regInfoControlsModel);
                        this.setCertReq(regInfoCertRequestModel);
                    }
                }
    
                this.setUtf8pairs(utf8PairsList);
        }
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
