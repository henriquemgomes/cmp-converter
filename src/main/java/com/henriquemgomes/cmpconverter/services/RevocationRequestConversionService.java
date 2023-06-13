package com.henriquemgomes.cmpconverter.services;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.cmp.PKIBody;
import org.bouncycastle.asn1.cmp.RevDetails;
import org.bouncycastle.asn1.cmp.RevReqContent;
import org.bouncycastle.asn1.crmf.CertTemplate;
import org.bouncycastle.asn1.x509.CRLReason;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.henriquemgomes.cmpconverter.dtos.CreateMessageDto;
import com.henriquemgomes.cmpconverter.exceptions.CmpConverterException;
import com.henriquemgomes.cmpconverter.interfaces.BodyConverterInterface;
import com.henriquemgomes.cmpconverter.models.PKIBodyModel;
import com.henriquemgomes.cmpconverter.models.RevDetailsModel;
import com.henriquemgomes.cmpconverter.models.RevReqContentModel;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RevocationRequestConversionService implements BodyConverterInterface{

    @Autowired
    private CertRequestService certRequestService;

    public int getType() {
        return PKIBody.TYPE_REVOCATION_REQ;
    }

    public ASN1Encodable getEncodable(byte[] content) {
        return RevReqContent.getInstance(content);
    }

    public PKIBodyModel createBodyModel(PKIBody pkiBody) throws CmpConverterException{
        RevReqContentModel pkiBodyModel = new RevReqContentModel();
        RevReqContent revReqContent = RevReqContent.getInstance(pkiBody.getContent());
        
        pkiBodyModel.instantiate(revReqContent);

        return pkiBodyModel;
    }

    @Override
    public byte[] convertToCmp(CreateMessageDto createMessageDto) throws Exception {
        try {
            log.info("Converting RevocationRequest to CMP.");
            RevReqContentModel revReqContentModel = createMessageDto.getRevReqContentModel();
            List<RevDetailsModel> revDetailsModels = revReqContentModel.getRevDetails();

            List<RevDetails> revDetailsList = new ArrayList<>();

            for (RevDetailsModel revDetailsModel : revDetailsModels) {

                CertTemplate certTemplate = this.certRequestService.createCertTemplate(revDetailsModel.getCertDetails(), createMessageDto.getExtraCerts());
                
                ExtensionsGenerator extGen = new ExtensionsGenerator();

                if (revDetailsModel.getCrlEntryDetails() != null) {
                    if(revDetailsModel.getCrlEntryDetails().getReasonCode() != null)
                        extGen.addExtension(Extension.reasonCode, false, CRLReason.lookup(this.getReasonId(revDetailsModel.getCrlEntryDetails().getReasonCode().getValue())));
                }
                
                
                RevDetails revDetails = new RevDetails(certTemplate, extGen.generate());


                revDetailsList.add(revDetails);
            }


            RevReqContent revReqContent = new RevReqContent(revDetailsList.toArray(new RevDetails[] {}));

            return revReqContent.getEncoded();   
        } catch (Exception ex) {
            String[] logInfo = {ex.getMessage()};
			throw new CmpConverterException(
				"cmp.error.generate.rr.cmp",
				"Failed to create revocation request cmp: "+ ex.getMessage(),
				300,
				HttpStatus.INTERNAL_SERVER_ERROR,
				logInfo
			);
        }

    }
    
    private int getReasonId(String reason) throws CmpConverterException {
        switch (reason) {
            case "unspecified":
                return 0;
            case "keyCompromise":
                return 1;
            case "cACompromise":
                return 2;
            case "affiliationChanged":
                return 3;
            case "superseded":
                return 4;
            case "cessationOfOperation":
                return 5;
            case "certificateHold":
                return 6;
            case "removeFromCRL":
                return 8;
            case "privilegeWithdrawn":
                return 9;
            case "aACompromise":
                return 10;
            default:
                throw new CmpConverterException(
                    "cmp.error.create.revocation.request.reason",
                    "Invalid reason provided in crl entry extension",
                    33000,
                    HttpStatus.BAD_REQUEST,
                    null
                );
        }
    }
}
