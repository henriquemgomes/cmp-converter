package com.henriquemgomes.cmpconverter.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DistinguishedNameModel {
    @JsonProperty("common_name")
    String commonName;

    String email;
    String organization;

    @JsonProperty("organizational_units")
    List<String> organizationalUnits;

    String locality;

    @JsonProperty("state_or_province")
    String stateOrProvince;
    String country;

    public DistinguishedNameModel() {
    }

    public String getCommonName() {
        return this.commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOrganization() {
        return this.organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public List<String> getOrganizationalUnits() {
        return this.organizationalUnits;
    }

    public void setOrganizationalUnits(List<String> organizationalUnits) {
        this.organizationalUnits = organizationalUnits;
    }

    public String getLocality() {
        return this.locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getStateOrProvince() {
        return this.stateOrProvince;
    }

    public void setStateOrProvince(String stateOrProvince) {
        this.stateOrProvince = stateOrProvince;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

}
