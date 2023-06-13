package com.henriquemgomes.cmpconverter.models;

import jakarta.validation.constraints.NotNull;

/*
    AttributeTypeAndValue ::= SEQUENCE {
        type         OBJECT IDENTIFIER,
        value        ANY DEFINED BY type 
    }
 */
public class AttributeTypeAndValueModel {
    
    @NotNull(message = "body.cert_req_messages.cert_req.reg_info.value is required")
    private String type;

    @NotNull(message = "body.cert_req_messages.cert_req.reg_info.type is required")
    private String value;

    public AttributeTypeAndValueModel() {
    }

    public AttributeTypeAndValueModel(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
