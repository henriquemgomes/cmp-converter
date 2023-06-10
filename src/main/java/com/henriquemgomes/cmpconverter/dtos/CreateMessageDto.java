package com.henriquemgomes.cmpconverter.dtos;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.henriquemgomes.cmpconverter.deserializers.PKIBodyOptionsDeserializer;
import com.henriquemgomes.cmpconverter.enums.PKIBodyOptions;
import com.henriquemgomes.cmpconverter.models.CertRepMessageModel;
import com.henriquemgomes.cmpconverter.models.CertificationRequestModel;
import com.henriquemgomes.cmpconverter.models.ExtraCertsModel;
import com.henriquemgomes.cmpconverter.models.PKIBodyModel;
// import com.henriquemgomes.cmpconverter.models.PKIBodyModel;
import com.henriquemgomes.cmpconverter.models.PKIHeaderModel;
// import com.henriquemgomes.cmpconverter.validation.Conditional;
import com.henriquemgomes.cmpconverter.validation.Conditional;
import com.henriquemgomes.serializers.CreateMessageDtoSerializer;

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
@Conditional(selected = "type", values = {"cp"}, required = {"certRepMessage"}, message = "body is required")
@JsonSerialize(using = CreateMessageDtoSerializer.class)
public class CreateMessageDto {

    public CreateMessageDto(PKIBodyOptions type, PKIHeaderModel header, List<ExtraCertsModel> extraCerts) {
        this.type = type;
        this.header = header;
        this.extraCerts = extraCerts;
    }

    @NotNull(message = "type is required")
    @JsonDeserialize(using = PKIBodyOptionsDeserializer.class)
    private PKIBodyOptions type;
    
    @NotNull(message = "header is required")
    @Valid
    private PKIHeaderModel header;

    @JsonProperty("extra_certs")
    private List<ExtraCertsModel> extraCerts;

    @JsonProperty("body")
    @Valid
    private CertificationRequestModel certificationRequest;

    @JsonProperty("body")
    @Valid
    private CertRepMessageModel certRepMessage;

    // @NotNull(message = "body is required")
    // @Valid
    // private PKIBodyModel body;

    public void setBody(PKIBodyModel body) {
        System.out.println(body.getType());
        switch (body.getType()) {
            case "cr":
                this.certificationRequest = (CertificationRequestModel) body;
                break;
            case "cp":
                this.certRepMessage = (CertRepMessageModel) body;
                break;
            default:
                break;
        }
    }

}
