package com.henriquemgomes.cmpconverter.models;

import org.bouncycastle.asn1.crmf.AttributeTypeAndValue;
import org.bouncycastle.asn1.crmf.CRMFObjectIdentifiers;
import org.bouncycastle.asn1.crmf.Controls;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import lombok.Data;



//Controls  ::= SEQUENCE SIZE(1..MAX) OF AttributeTypeAndValue

@Data
public class ControlsModel {
    
    @JsonProperty("authenticator_control")
    @Valid
    private BasicControlModel authenticatorControl;

    @JsonProperty("reg_token_control")
    @Valid
    private BasicControlModel regTokenControl;

    public ControlsModel() {
    }

    public ControlsModel(Controls controls) {
        AttributeTypeAndValue[] controlsArray = controls.toAttributeTypeAndValueArray();

        for (AttributeTypeAndValue attributeTypeAndValue : controlsArray) {
            BasicControlModel basicControlModel = new BasicControlModel();
            if(attributeTypeAndValue.getType().toString().equals(CRMFObjectIdentifiers.id_regCtrl_authenticator.toString())){
                basicControlModel.setValue(attributeTypeAndValue.getValue().toString());
                this.setAuthenticatorControl(basicControlModel);
            }

            if(attributeTypeAndValue.getType().equals(CRMFObjectIdentifiers.id_regCtrl_regToken)){
                basicControlModel.setValue(attributeTypeAndValue.getValue().toString());
                this.setRegTokenControl(basicControlModel);
            }
        }
    }


}
