package com.henriquemgomes.cmpconverter.dtos;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.henriquemgomes.cmpconverter.enums.PKIBodyOptions;
import com.henriquemgomes.cmpconverter.models.CertificationRequestModel;
import com.henriquemgomes.cmpconverter.models.ExtraCertsModel;
import com.henriquemgomes.cmpconverter.models.PKIBodyModel;
// import com.henriquemgomes.cmpconverter.models.PKIBodyModel;
import com.henriquemgomes.cmpconverter.models.PKIHeaderModel;
// import com.henriquemgomes.cmpconverter.validation.Conditional;
import com.henriquemgomes.cmpconverter.validation.Conditional;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

// import javax.validation.Valid;

@Getter
@Setter
@Conditional(selected = "type", values = {"cr"}, required = {"certificationRequest"}, message = "body is required")
public class CreateMessageDto {

    public CreateMessageDto() {
    }

    @NotNull(message = "type is required")
    private PKIBodyOptions type;
    
    @NotNull(message = "header is required")
    @Valid
    private PKIHeaderModel header;

    @JsonProperty("extra_certs")
    private List<ExtraCertsModel> extraCerts;

    @JsonProperty("body")
    @Valid
    private CertificationRequestModel certificationRequest;

    // @NotNull(message = "body is required")
    // @Valid
    // private PKIBodyModel body;

    public void setBody(PKIBodyModel body) {
        switch (body.getType()) {
            case "cr":
                this.certificationRequest = (CertificationRequestModel) body;
                break;
        
            default:
                break;
        }
    }

}
