package com.henriquemgomes.cmpconverter.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;



//Controls  ::= SEQUENCE SIZE(1..MAX) OF AttributeTypeAndValue

public class ControlsModel {
    
    @NotNull(message = "body.cert_req_messages.cert_req.cert_req_id.controls.attribute_type_and_value cannot be null")
    @NotEmpty(message = "body.cert_req_messages.cert_req.cert_req_id.controls.attribute_type_and_value cannot be empty")
    @JsonProperty("attribute_type_and_value")
    private List<@Valid AttributeTypeAndValueModel> attributeTypeAndValueModels;

    public ControlsModel() {
    }

    public ControlsModel(List<AttributeTypeAndValueModel> attributeTypeAndValueModels) {
        this.attributeTypeAndValueModels = attributeTypeAndValueModels;
    }

    public List<AttributeTypeAndValueModel> getAttributeTypeAndValueModels() {
        return this.attributeTypeAndValueModels;
    }

    public void setAttributeTypeAndValueModels(List<AttributeTypeAndValueModel> attributeTypeAndValueModels) {
        this.attributeTypeAndValueModels = attributeTypeAndValueModels;
    }
    
}
