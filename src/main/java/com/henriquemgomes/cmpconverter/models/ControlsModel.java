package com.henriquemgomes.cmpconverter.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import lombok.Data;



//Controls  ::= SEQUENCE SIZE(1..MAX) OF AttributeTypeAndValue

@Data
public class ControlsModel {
    
    @JsonProperty("authenticator_control")
    @Valid
    private BasicControlModel authenticatorControl;

    @JsonProperty("reg_token_control")
    @Valid
    private BasicControlModel regTokenControl;

    public ControlsModel() {
    }


}
