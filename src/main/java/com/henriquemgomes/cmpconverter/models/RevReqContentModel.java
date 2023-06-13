package com.henriquemgomes.cmpconverter.models;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.asn1.cmp.RevDetails;
import org.bouncycastle.asn1.cmp.RevReqContent;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.henriquemgomes.cmpconverter.exceptions.CmpConverterException;

public class RevReqContentModel extends PKIBodyModel {

    @JsonProperty("rev_details")
    private List<RevDetailsModel> revDetails;

    public RevReqContentModel() {
        super(PKIBodyModel.TYPE_REVOCATION_REQUEST);    
    }

    public List<RevDetailsModel> getRevDetails() {
        return this.revDetails;
    }

    public void setRevDetails(List<RevDetailsModel> revDetails) {
        this.revDetails = revDetails;
    }

    public void instantiate(RevReqContent revReqContent) throws CmpConverterException {
        try {
            List<RevDetailsModel> revDetailsModelsList = new ArrayList<>();

            RevDetails[] revDetails = revReqContent.toRevDetailsArray();

            for (RevDetails revDetail : revDetails) {
                RevDetailsModel revDetailsModel = new RevDetailsModel(revDetail);
                revDetailsModelsList.add(revDetailsModel);
            }

            this.setRevDetails(revDetailsModelsList);
           
        } catch (Exception e) {
            throw new CmpConverterException(
                    "cmp.error.convert.revocation.request.json",
                    "Failed to conver revocation request to json.",
                    34000,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    null
                );
        }
    }

}
