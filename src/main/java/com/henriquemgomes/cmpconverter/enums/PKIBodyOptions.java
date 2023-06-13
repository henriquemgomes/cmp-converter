package com.henriquemgomes.cmpconverter.enums;

public enum PKIBodyOptions {
    cr("cr"),
    ir("ir"),
    cp("cp"),
    rr("rr"),
    rp("rp");
    public final String type;

    PKIBodyOptions(String type) {
        this.type = type;
    }
}
