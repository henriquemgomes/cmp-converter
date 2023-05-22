package com.henriquemgomes.cmpconverter.models;

public class UTF8PairsModel {
    
    private String value;

    public UTF8PairsModel() {

    }
  
    public UTF8PairsModel(String value){
        this.value = value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
