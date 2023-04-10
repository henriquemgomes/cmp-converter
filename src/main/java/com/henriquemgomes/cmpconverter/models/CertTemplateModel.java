package com.henriquemgomes.cmpconverter.models;

import com.fasterxml.jackson.annotation.JsonProperty;

/*
    CertTemplate ::= SEQUENCE {
        version      [0] Version               OPTIONAL,
        serialNumber [1] INTEGER               OPTIONAL,
        signingAlg   [2] AlgorithmIdentifier   OPTIONAL,
        issuer       [3] Name                  OPTIONAL,
        validity     [4] OptionalValidity      OPTIONAL,
        subject      [5] Name                  OPTIONAL,
        publicKey    [6] SubjectPublicKeyInfo  OPTIONAL,
        issuerUID    [7] UniqueIdentifier      OPTIONAL,
        subjectUID   [8] UniqueIdentifier      OPTIONAL,
        extensions   [9] Extensions            OPTIONAL 
    }
 */

public class CertTemplateModel {

    private String version;

    @JsonProperty("serial_number")
    private String serialNumber;

    @JsonProperty("signing_alg")
    private String signingAlg;

    private String issuer;

    private OptionalValidityModel validity;
    
    private String subject;

    @JsonProperty("public_key")
    private String publicKey;

    @JsonProperty("issuer_uid")
    private String issuerUID;

    @JsonProperty("subject_uid")
    private String subjectUID;

    private String extensions;

    public CertTemplateModel() {
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSerialNumber() {
        return this.serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getSigningAlg() {
        return this.signingAlg;
    }

    public void setSigningAlg(String signingAlg) {
        this.signingAlg = signingAlg;
    }

    public String getIssuer() {
        return this.issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public OptionalValidityModel getValidity() {
        return this.validity;
    }

    public void setValidity(OptionalValidityModel validity) {
        this.validity = validity;
    }

    public String getSubject() {
        return this.subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPublicKey() {
        return this.publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getIssuerUID() {
        return this.issuerUID;
    }

    public void setIssuerUID(String issuerUID) {
        this.issuerUID = issuerUID;
    }

    public String getSubjectUID() {
        return this.subjectUID;
    }

    public void setSubjectUID(String subjectUID) {
        this.subjectUID = subjectUID;
    }

    public String getExtensions() {
        return this.extensions;
    }

    public void setExtensions(String extensions) {
        this.extensions = extensions;
    }
}
