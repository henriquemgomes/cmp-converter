package com.henriquemgomes.cmpconverter.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.tomcat.util.codec.binary.Base64;
import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.cmp.CMPCertificate;
import org.bouncycastle.asn1.cmp.CertRepMessage;
import org.bouncycastle.asn1.cmp.CertResponse;

public class CertRepMessageModel extends PKIBodyModel {

    private List<String> caPubs;

    private List<CertResponseModel> response;

    public CertRepMessageModel() {
        super(PKIBodyModel.TYPE_CERT_REP);
    }

    public List<String> getCaPubs() {
        return this.caPubs;
    }

    public void setCaPubs(List<String> caPubs) {
        this.caPubs = caPubs;
    }

    public List<CertResponseModel> getResponse() {
        return this.response;
    }

    public void setResponse(List<CertResponseModel> response) {
        this.response = response;
    }

    public CertRepMessageModel instantiate(CertRepMessage certRepMessage) throws IOException {
        this.caPubs = new ArrayList<>();

        CMPCertificate[] caPubs = certRepMessage.getCaPubs();
        for (CMPCertificate cmpCertificate : caPubs) {
            this.caPubs.add(Base64.encodeBase64String(cmpCertificate.getX509v3PKCert().getEncoded(ASN1Encoding.DER)));
        }

        this.response = new ArrayList<>();

        CertResponse[] certResponses = certRepMessage.getResponse();
        for (CertResponse certResponse : certResponses) {
            this.response.add(new CertResponseModel(certResponse));
        }
        return null;
    }
   
}
