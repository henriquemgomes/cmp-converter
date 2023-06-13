package com.henriquemgomes.cmpconverter.models;

import java.io.IOException;

import org.bouncycastle.asn1.cmp.RevDetails;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.henriquemgomes.cmpconverter.deserializers.CertTemplateModelDeserializer;

public class RevDetailsModel {

    @JsonProperty("cert_details")
    @JsonDeserialize(using = CertTemplateModelDeserializer.class)
    private CertTemplateModel certDetails;

    @JsonProperty("crl_entry_details")
    private ExtensionsModel crlEntryDetails;

    public RevDetailsModel() {
    }

    public RevDetailsModel(RevDetails revDetail) throws IOException {
        CertTemplateModel certDetails = new CertTemplateModel(revDetail.getCertDetails());
        this.certDetails = certDetails;

        Extensions crlExtensions = revDetail.getCrlEntryDetails();
        ExtensionsModel extensionsModel = new ExtensionsModel(crlExtensions);
        this.crlEntryDetails = extensionsModel;
    }

    public CertTemplateModel getCertDetails() {
        return this.certDetails;
    }

    public void setCertDetails(CertTemplateModel certDetails) {
        this.certDetails = certDetails;
    }

    public ExtensionsModel getCrlEntryDetails() {
        return this.crlEntryDetails;
    }

    public void setCrlEntryDetails(ExtensionsModel crlEntryDetails) {
        this.crlEntryDetails = crlEntryDetails;
    }


}
