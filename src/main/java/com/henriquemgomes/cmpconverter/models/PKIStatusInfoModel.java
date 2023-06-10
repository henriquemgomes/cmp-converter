package com.henriquemgomes.cmpconverter.models;

import org.bouncycastle.asn1.cmp.PKIStatusInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PKIStatusInfoModel {
    private String status;

    @JsonProperty("status_string")
    private String statusString;

    @JsonProperty("pki_failure_info")
    private String pkiFailureInfo;

    public PKIStatusInfoModel() {
    }

    public PKIStatusInfoModel(PKIStatusInfo status) {
        if (status.getStatusString() != null)
            this.statusString = status.getStatusString().toString();

        if(status.getFailInfo() != null)
            this.pkiFailureInfo = status.getFailInfo().getString();
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusString() {
        return this.statusString;
    }

    public void setStatusString(String statusString) {
        this.statusString = statusString;
    }

    public String getPkiFailureInfo() {
        return this.pkiFailureInfo;
    }

    public void setPkiFailureInfo(String pkiFailureInfo) {
        this.pkiFailureInfo = pkiFailureInfo;
    }

}
