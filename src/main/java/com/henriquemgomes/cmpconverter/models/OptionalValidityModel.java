package com.henriquemgomes.cmpconverter.models;

import com.fasterxml.jackson.annotation.JsonProperty;

/*
    OptionalValidity ::= SEQUENCE {
        notBefore  [0] Time OPTIONAL,
        notAfter   [1] Time OPTIONAL 
    }
 */

 
public class OptionalValidityModel {
    
    @JsonProperty("not_before")
    private String notBefore;

    @JsonProperty("not_after")
    private String notAfter;

    public OptionalValidityModel() {
    }

    public OptionalValidityModel(String notBefore, String notAfter) {
        this.notBefore = notBefore;
        this.notAfter = notAfter;
    }

    public String getNotBefore() {
        return this.notBefore;
    }

    public void setNotBefore(String notBefore) {
        this.notBefore = notBefore;
    }

    public String getNotAfter() {
        return this.notAfter;
    }

    public void setNotAfter(String notAfter) {
        this.notAfter = notAfter;
    }

}
