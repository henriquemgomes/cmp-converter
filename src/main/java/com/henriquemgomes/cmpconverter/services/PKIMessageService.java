package com.henriquemgomes.cmpconverter.services;

import java.io.IOException;

import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.cmp.PKIMessage;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.stereotype.Service;

import com.henriquemgomes.cmpconverter.dtos.AttatchPKIMessageSignatureDto;

@Service
public class PKIMessageService {

    public PKIMessageService() {
    }

    public byte[] attatchSignature(AttatchPKIMessageSignatureDto attatchPKIMessageSignatureDto) throws IOException {
        PKIMessage pkiMessage = PKIMessage.getInstance(Base64.decode(attatchPKIMessageSignatureDto.getPkiMessage()));
        ASN1BitString signature = (ASN1BitString) ASN1BitString.fromByteArray(Base64.decode(attatchPKIMessageSignatureDto.getPkiMessage()));

        pkiMessage = new PKIMessage(pkiMessage.getHeader(), pkiMessage.getBody(), signature, pkiMessage.getExtraCerts());

        return pkiMessage.getEncoded();
    }

    
}
