package com.henriquemgomes.cmpconverter.models;





public class PKIMessageModel {

    private PKIHeaderModel pkiHeader;

    private PKIBodyModel pkiBody;

    public PKIMessageModel() {
    }

    public PKIMessageModel(PKIHeaderModel pkiHeader, PKIBodyModel pkiBody) {
        this.pkiHeader = pkiHeader;
        this.pkiBody = pkiBody;
    }

    public PKIHeaderModel getPkiHeader() {
        return this.pkiHeader;
    }

    public void setPkiHeader(PKIHeaderModel pkiHeader) {
        this.pkiHeader = pkiHeader;
    }

    public PKIBodyModel getPkiBody() {
        return this.pkiBody;
    }

    public void setPkiBody(PKIBodyModel pkiBody) {
        this.pkiBody = pkiBody;
    }

}
