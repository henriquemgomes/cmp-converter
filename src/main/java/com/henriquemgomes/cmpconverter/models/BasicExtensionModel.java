package com.henriquemgomes.cmpconverter.models;

import jakarta.validation.constraints.NotNull;

public class BasicExtensionModel {
    @NotNull(message = "Extension value cannot be null")
    private String value;

    public BasicExtensionModel() {

    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

}
