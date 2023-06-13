package com.henriquemgomes.cmpconverter.services;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.cmp.PKIBody;
import org.bouncycastle.asn1.cmp.RevRepContent;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.henriquemgomes.cmpconverter.dtos.CreateMessageDto;
import com.henriquemgomes.cmpconverter.exceptions.CmpConverterException;
import com.henriquemgomes.cmpconverter.interfaces.BodyConverterInterface;
import com.henriquemgomes.cmpconverter.models.PKIBodyModel;
import com.henriquemgomes.cmpconverter.models.RevRepContentModel;

@Service
public class RevocationResponseConversionService implements BodyConverterInterface {

    public int getType() {
        return PKIBody.TYPE_REVOCATION_REP;
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
            RevRepContentModel pkiBodyModel = new RevRepContentModel();
            RevRepContent revRepContent = RevRepContent.getInstance(pkiBody.getContent());
            
            pkiBodyModel.instantiate(revRepContent);
    
            return pkiBodyModel;
        } catch (Exception e) {
            String[] logInfo = {e.getMessage()};
			throw new CmpConverterException(
				"cmp.error.create.rp.json",
				"Failed to create rp body from given cmp message: "+ e.getMessage(),
				3500,
				HttpStatus.INTERNAL_SERVER_ERROR,
				logInfo
			);
        }
       
    }
}
