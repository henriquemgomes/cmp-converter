package com.henriquemgomes.cmpconverter.dtos;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.henriquemgomes.cmpconverter.models.CertRequestModel;
import com.henriquemgomes.cmpconverter.models.CertTemplateModel;
import com.henriquemgomes.cmpconverter.models.ExtraCertsModel;

public class CreateCertTemplateDto {

    @JsonProperty("cert_template")
    private CertTemplateModel certTemplate;

    @JsonProperty("extra_certs")
    private List<ExtraCertsModel> extraCerts;

    public CreateCertTemplateDto() {
    }

    public CertTemplateModel getCertTemplate() {
        return this.certTemplate;
    }

    public void setCertTemplate(CertTemplateModel certTemplate) {
        this.certTemplate = certTemplate;
    }

    public List<ExtraCertsModel> getExtraCerts() {
        return this.extraCerts;
    }

    public void setExtraCerts(List<ExtraCertsModel> extraCerts) {
        this.extraCerts = extraCerts;
    }    
}
