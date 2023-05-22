package com.henriquemgomes.cmpconverter.interfaces;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.cmp.PKIBody;

import com.henriquemgomes.cmpconverter.exceptions.CmpConverterException;
import com.henriquemgomes.cmpconverter.models.PKIBodyModel;

public interface BodyConverterInterface extends ConversionInterface {
    public int getType();
    public ASN1Encodable getEncodable(byte[] content);
    public PKIBodyModel createBodyModel(PKIBody pkiBody) throws CmpConverterException;
}
