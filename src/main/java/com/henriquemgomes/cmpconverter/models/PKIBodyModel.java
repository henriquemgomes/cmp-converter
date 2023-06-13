package com.henriquemgomes.cmpconverter.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class PKIBodyModel {

    public static final String TYPE_INIT_REQ = "ir";
    public static final String TYPE_INIT_REP = "ip";
    public static final String TYPE_CERT_REQ = "cr";
    public static final String TYPE_CERT_REP = "cp";
    public static final String TYPE_REVOCATION_REQUEST = "rr";
    public static final String TYPE_REVOCATION_RESPONSE = "rp";

    @JsonIgnore
    private String type;

    public PKIBodyModel() {
    }
    
    public PKIBodyModel(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
