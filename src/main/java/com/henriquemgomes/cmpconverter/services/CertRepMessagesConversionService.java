package com.henriquemgomes.cmpconverter.services;

import java.io.IOException;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.cmp.CertRepMessage;
import org.bouncycastle.asn1.cmp.PKIBody;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.henriquemgomes.cmpconverter.dtos.CreateMessageDto;
import com.henriquemgomes.cmpconverter.exceptions.CmpConverterException;
import com.henriquemgomes.cmpconverter.interfaces.BodyConverterInterface;
import com.henriquemgomes.cmpconverter.models.CertRepMessageModel;
import com.henriquemgomes.cmpconverter.models.PKIBodyModel;

@Service
public class CertRepMessagesConversionService implements BodyConverterInterface {

    public int getType() {
        return PKIBody.TYPE_CERT_REP;
    }

    public ASN1Encodable getEncodable(byte[] content) {
        return null;
    }
    
    public byte[] convertToCmp(CreateMessageDto createMessageDto) {
        return null;
    }

    @Override
    public PKIBodyModel createBodyModel(PKIBody pkiBody) throws CmpConverterException {
        try {
            CertRepMessageModel pkiBodyModel = new CertRepMessageModel();
            CertRepMessage certRepMessage = CertRepMessage.getInstance(pkiBody.getContent());
            
            pkiBodyModel.instantiate(certRepMessage);
    
            return pkiBodyModel;
        } catch (Exception e) {
            String[] logInfo = {e.getMessage()};
			throw new CmpConverterException(
				"cmp.error.instantiate.pubkey",
				"Could not create cp body from given cmp message: "+ e.getMessage(),
				1100,
				HttpStatus.INTERNAL_SERVER_ERROR,
				logInfo
			);
        }
       
    }
}
