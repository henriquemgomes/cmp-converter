package com.henriquemgomes.cmpconverter.models;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.tomcat.util.codec.binary.Base64;
import org.bouncycastle.asn1.cmp.PKIStatusInfo;
import org.bouncycastle.asn1.cmp.RevRepContent;
import org.bouncycastle.asn1.crmf.CertId;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.henriquemgomes.cmpconverter.exceptions.CmpConverterException;

import lombok.Data;

@Data
public class RevRepContentModel extends PKIBodyModel {
    
    private List<PKIStatusInfoModel> status;

    @JsonProperty("rev_certs")
    private List<String> revCerts;

    public RevRepContentModel() {
        super(PKIBodyModel.TYPE_REVOCATION_RESPONSE);
    }

    public void instantiate(RevRepContent revRepContent) throws CmpConverterException {
        try {
            PKIStatusInfo[] pkiStatusInfos = revRepContent.getStatus();
        
            List<PKIStatusInfoModel> statusList = new ArrayList<>();

            for (PKIStatusInfo pkiStatusInfo : pkiStatusInfos) {
                PKIStatusInfoModel statusInfoModel = new PKIStatusInfoModel(pkiStatusInfo);
                statusList.add(statusInfoModel);
            }

            CertId[] certIds = revRepContent.getRevCerts();

            List<String> certIdsList = new ArrayList<>();

            if(certIds != null) {
                for (CertId certId : certIds) {
                    certIdsList.add(Base64.encodeBase64String(certId.getEncoded()));
                }
            }

            this.setStatus(statusList);
            this.setRevCerts(certIdsList);
        } catch (Exception e) {

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);

            throw new CmpConverterException(
                    "cmp.error.convert.revocation.response.json",
                    "Failed to conver revocation response to json."+ sw.toString(),
                    34001,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    null
                );
        }
        
    }
}
