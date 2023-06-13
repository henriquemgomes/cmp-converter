package com.henriquemgomes.cmpconverter.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import lombok.Data;



//Controls  ::= SEQUENCE SIZE(1..MAX) OF AttributeTypeAndValue

@Data
public class InfoTypeAndValueModel {
    
    @JsonProperty("implicit_confirm")
    @Valid
    private BasicInfoTypeAndValueModel implicitConfirm;

    @JsonProperty("confirm_wait_time")
    @Valid
    private BasicInfoTypeAndValueModel confirmWaitTime;

    public InfoTypeAndValueModel() {
        if(this.implicitConfirm != null){
            this.implicitConfirm.setValue(null);
        }
    }


}
