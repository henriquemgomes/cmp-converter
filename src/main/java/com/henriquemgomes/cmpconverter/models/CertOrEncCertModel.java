package com.henriquemgomes.cmpconverter.models;

import java.io.IOException;

import org.apache.tomcat.util.codec.binary.Base64;
import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.cmp.CertOrEncCert;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CertOrEncCertModel {

    private String certificate;

    @JsonProperty("encrypted_cert")
    private String encryptedCert;

    public CertOrEncCertModel() {
    }

    public CertOrEncCertModel(CertOrEncCert certOrEncCert) throws IOException {
        if(certOrEncCert.getCertificate() != null)
            this.certificate = Base64.encodeBase64String(certOrEncCert.getCertificate().getX509v3PKCert().getEncoded(ASN1Encoding.DER));

        if(certOrEncCert.getEncryptedCert() != null)
            this.certificate = Base64.encodeBase64String(certOrEncCert.getEncryptedCert().getValue().toASN1Primitive().getEncoded((ASN1Encoding.DER)));
    }

    public String getCertificate() {
        return this.certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public String getEncryptedCert() {
        return this.encryptedCert;
    }

    public void setEncryptedCert(String encryptedCert) {
        this.encryptedCert = encryptedCert;
    }

}
