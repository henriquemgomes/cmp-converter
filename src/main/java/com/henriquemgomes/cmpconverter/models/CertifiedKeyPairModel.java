package com.henriquemgomes.cmpconverter.models;

import java.io.IOException;

import org.apache.tomcat.util.codec.binary.Base64;
import org.bouncycastle.asn1.cmp.CertifiedKeyPair;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CertifiedKeyPairModel {

    @JsonProperty("cert_or_enc_cert")
    private CertOrEncCertModel certOrEncCert;

    @JsonProperty("private_key")
    private String privateKey;

    @JsonProperty("publication_info")
    private String publicationInfo;

    public CertifiedKeyPairModel() {
    }

    public CertifiedKeyPairModel(CertifiedKeyPair certifiedKeyPair) throws IOException {
        if(certifiedKeyPair.getCertOrEncCert() != null)
            this.certOrEncCert = new CertOrEncCertModel(certifiedKeyPair.getCertOrEncCert());
        
        if(certifiedKeyPair.getPrivateKey() != null)
            this.privateKey = Base64.encodeBase64String(certifiedKeyPair.getPrivateKey().getValue().toASN1Primitive().getEncoded());

        if(certifiedKeyPair.getPublicationInfo() != null)
            this.publicationInfo = Base64.encodeBase64String(certifiedKeyPair.getPublicationInfo().getEncoded());
    }

    public CertOrEncCertModel getCertOrEncCert() {
        return this.certOrEncCert;
    }

    public void setCertOrEncCert(CertOrEncCertModel certOrEncCert) {
        this.certOrEncCert = certOrEncCert;
    }

    public String getPrivateKey() {
        return this.privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPublicationInfo() {
        return this.publicationInfo;
    }

    public void setPublicationInfo(String publicationInfo) {
        this.publicationInfo = publicationInfo;
    }

    
}
