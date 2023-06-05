package com.henriquemgomes.cmpconverter.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.henriquemgomes.cmpconverter.deserializers.ForceStringDeserializer;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class SubjectAltNameModel {
    @JsonProperty("oid")
	private String oid;

	@JsonProperty("value")   
    @NotNull(message = "body.cert_req_messages.Array<cert_req>.cert_template.extensions.Array<subject_alt_names>.value is required")
    @NotEmpty(message = "body.cert_req_messages.Array<cert_req>.cert_template.extensions.Array<subject_alt_names>.value cannot be empty")
	private String value;

	@JsonProperty("general_name")
    @JsonDeserialize(using = ForceStringDeserializer.class)
    @NotNull(message = "body.cert_req_messages.Array<cert_req>.cert_template.extensions.Array<subject_alt_names>.general_name is required")
    @NotEmpty(message = "body.cert_req_messages.Array<cert_req>.cert_template.extensions.Array<subject_alt_names>.general_name cannot be empty")
	private String generalName;

    public SubjectAltNameModel() {
    }

    public SubjectAltNameModel(String oid, String value, String generalName) {
        this.oid = oid;
        this.value = value;
        this.generalName = generalName;
    }

    public String getOid() {
        return this.oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getGeneralName() {
        return this.generalName;
    }

    public void setGeneralName(String generalName) {
        this.generalName = generalName;
    }

}
