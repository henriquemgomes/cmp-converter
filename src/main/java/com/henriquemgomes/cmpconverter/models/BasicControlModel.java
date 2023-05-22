package com.henriquemgomes.cmpconverter.models;

import jakarta.validation.constraints.NotNull;

public class BasicControlModel {
    @NotNull(message = "Control value cannot be null")
    private String value;

    public BasicControlModel() {

    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

}
