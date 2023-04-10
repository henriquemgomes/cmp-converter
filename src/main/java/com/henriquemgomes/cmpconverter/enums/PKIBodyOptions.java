package com.henriquemgomes.cmpconverter.enums;

public enum PKIBodyOptions {
    cr("cr"),
    ir("ir");
    public final String type;

    PKIBodyOptions(String type) {
        this.type = type;
    }
}
