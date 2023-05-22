package com.henriquemgomes.cmpconverter.services;

import java.util.Map;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.cmp.PKIBody;
import org.bouncycastle.asn1.cmp.PKIHeader;
import org.bouncycastle.asn1.cmp.PKIMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.henriquemgomes.cmpconverter.Utils;
import com.henriquemgomes.cmpconverter.dtos.CreateMessageDto;
import com.henriquemgomes.cmpconverter.enums.PKIBodyOptions;
import com.henriquemgomes.cmpconverter.exceptions.CmpConverterException;
import com.henriquemgomes.cmpconverter.interfaces.ConversionInterface;
import com.henriquemgomes.cmpconverter.models.PKIBodyModel;
import com.henriquemgomes.cmpconverter.interfaces.BodyConverterInterface;

@Service
public class PKIMessageConversionService implements ConversionInterface {

    @Autowired
    private Map<String, BodyConverterInterface> conversionServices;

    @Autowired
    private PKIHeaderConversionService pkiHeaderConversionService;

    private BodyConverterInterface getDynamicConversionService(PKIBodyOptions messageType) {
        switch (messageType) {
            case ir:
            case cr:
                return conversionServices.get("certReqMessagesConversionService");
            default:
                System.out.println("Type not supported");
                return null;
        }
    }

    public byte[] convertToCmp(CreateMessageDto createMessageDto) throws Exception {
        PKIHeader pkiHeader = pkiHeaderConversionService.createPkiHeader(createMessageDto);
        
        BodyConverterInterface conversionService = this.getDynamicConversionService(createMessageDto.getType());
        byte[] bodyContent = (byte[]) conversionService.convertToCmp(createMessageDto);
        PKIBody pkiBody = new PKIBody(conversionService.getType(), conversionService.getEncodable(bodyContent));

        PKIMessage pkiMessage = new PKIMessage(pkiHeader, pkiBody);
        return pkiMessage.getEncoded();
    }

    public CreateMessageDto convertToJson(PKIMessage pkiMessage) throws CmpConverterException {
        PKIBody pkiBody = pkiMessage.getBody();
        PKIBodyOptions type = PKIBodyOptions.valueOf(Utils.translateCMPMessageType(pkiBody.getType()));
        BodyConverterInterface conversionService = this.getDynamicConversionService(type);

        PKIBodyModel pkiBodyModel = conversionService.createBodyModel(pkiBody);

        CreateMessageDto createMessageDto = new CreateMessageDto();
        createMessageDto.setType(type);
        createMessageDto.setBody(pkiBodyModel);

        return createMessageDto;
    }
 }
