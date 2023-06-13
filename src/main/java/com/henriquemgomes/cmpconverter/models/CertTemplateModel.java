package com.henriquemgomes.cmpconverter.models;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.apache.tomcat.util.codec.binary.Base64;
import org.bouncycastle.asn1.crmf.CertTemplate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.henriquemgomes.cmpconverter.Utils;
import com.henriquemgomes.cmpconverter.deserializers.CertTemplateModelDeserializer;

import jakarta.validation.Valid;

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

    private DistinguishedNameModel issuer;

    private OptionalValidityModel validity;
    
    private DistinguishedNameModel subject;

    @JsonProperty("public_key")
    private String publicKey;

    @JsonProperty("issuer_uid")
    private String issuerUID;

    @JsonProperty("subject_uid")
    private String subjectUID;

    @Valid()
    private ExtensionsModel extensions;

    public CertTemplateModel() {
    }

    public CertTemplateModel(CertTemplate certTemplate) throws IOException {

        if (certTemplate.getVersion() > 0) {
            this.setVersion(String.valueOf(certTemplate.getVersion()));
        }

        if(certTemplate.getValidity() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            sdf.setLenient(false);

            OptionalValidityModel optionalValidity = new OptionalValidityModel();

            if(certTemplate.getValidity().getNotBefore() != null)
                optionalValidity.setNotBefore(sdf.format(certTemplate.getValidity().getNotBefore().getDate()));

            if(certTemplate.getValidity().getNotAfter() != null)
                optionalValidity.setNotAfter(sdf.format(certTemplate.getValidity().getNotAfter().getDate()));

                this.setValidity(optionalValidity);
        }

        if (certTemplate.getIssuer() != null) {
            this.setIssuer(Utils.parseDn(certTemplate.getIssuer().getRDNs()));
        }

        if (certTemplate.getSubject() != null) {
            this.setSubject(Utils.parseDn(certTemplate.getSubject().getRDNs()));
        }

        if(certTemplate.getSerialNumber() != null) {
            String serialNumber = Utils.encodeToHexString(Utils.encodeBigInteger(certTemplate.getSerialNumber().getValue()));
            this.setSerialNumber(serialNumber);
        }

        if (certTemplate.getPublicKey() != null) {
            this.setPublicKey(Base64.encodeBase64String(certTemplate.getPublicKey().getEncoded()));
        }

        if (certTemplate.getSigningAlg() != null) {
            this.setSigningAlg(certTemplate.getSigningAlg().toString());
        }

        if (certTemplate.getExtensions() != null) {
            // ExtensionsModel regInfoExtensionsModel = this.generateExtensionsModel(certTemplate.getExtensions());   
            ExtensionsModel extensionsModel = new ExtensionsModel(certTemplate.getExtensions());   
            this.setExtensions(extensionsModel);
        }
        
        if (certTemplate.getSubjectUID() != null) {
            this.setSubjectUID(Utils.encodeToHexString(certTemplate.getSubjectUID().getBytes()));
        }

        if (certTemplate.getIssuerUID() != null) {
            this.setIssuerUID(Utils.encodeToHexString(certTemplate.getIssuerUID().getBytes()));
        }
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

    public DistinguishedNameModel getIssuer() {
        return this.issuer;
    }

    public void setIssuer(DistinguishedNameModel issuer) {
        this.issuer = issuer;
    }

    public OptionalValidityModel getValidity() {
        return this.validity;
    }

    public void setValidity(OptionalValidityModel validity) {
        this.validity = validity;
    }

    public DistinguishedNameModel getSubject() {
        return this.subject;
    }

    public void setSubject(DistinguishedNameModel subject) {
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

    public ExtensionsModel getExtensions() {
        return this.extensions;
    }

    public void setExtensions(ExtensionsModel extensions) {
        this.extensions = extensions;
    }
}
