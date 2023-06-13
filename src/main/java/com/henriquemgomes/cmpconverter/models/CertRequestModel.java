package com.henriquemgomes.cmpconverter.models;

import java.io.IOException;
import org.bouncycastle.asn1.crmf.CertRequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.henriquemgomes.cmpconverter.deserializers.CertTemplateModelDeserializer;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/*
    CertRequest ::= SEQUENCE {
        certReqId     INTEGER,          
        certTemplate  CertTemplate, 
        controls      Controls OPTIONAL 
    }
 */

public class CertRequestModel {

    @Positive(message = "body.cert_req_messages.cert_req.cert_req_id must be a positive integer")
    @NotNull(message = "body.cert_req_messages.cert_req.cert_req_id is required")
    @JsonProperty("cert_req_id")
    private int certReqId;

    @JsonProperty("cert_template")
    @NotNull(message = "body.cert_req_messages.cert_req.cert_req_id.cert_template is required")
    @JsonDeserialize(using = CertTemplateModelDeserializer.class)
    @Valid
    private CertTemplateModel certTemplate;

    private ControlsModel controls;

    public CertRequestModel() {
    }

    public CertRequestModel(CertRequest certRequest) throws IOException {
        CertTemplateModel certTemplateModel = new CertTemplateModel(certRequest.getCertTemplate());        
        this.setCertTemplate(certTemplateModel);

        ControlsModel controlsModel = new ControlsModel(certRequest.getControls());
        this.setControls(controlsModel);
    }

    public CertRequestModel(int certReqId, CertTemplateModel certTemplate) {
        this.certReqId = certReqId;
        this.certTemplate = certTemplate;
    }

    public CertRequestModel(int certReqId, CertTemplateModel certTemplate,  ControlsModel controls) {
        this.certReqId = certReqId;
        this.certTemplate = certTemplate;
        this.controls = controls;
    }

    public int getCertReqId() {
        return this.certReqId;
    }

    public void setCertReqId(int certReqId) {
        this.certReqId = certReqId;
    }

    public CertTemplateModel getCertTemplate() {
        return this.certTemplate;
    }

    public void setCertTemplate(CertTemplateModel certTemplate) {
        this.certTemplate = certTemplate;
    }

    public ControlsModel getControls() {
        return this.controls;
    }

    public void setControls(ControlsModel controls) {
        this.controls = controls;
    }

}
