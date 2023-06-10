package com.henriquemgomes.cmpconverter.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.tomcat.util.codec.binary.Base64;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.cmp.CMPCertificate;
import org.bouncycastle.asn1.cmp.PKIBody;
import org.bouncycastle.asn1.cmp.PKIHeader;
import org.bouncycastle.asn1.cmp.PKIMessage;
import org.bouncycastle.asn1.x509.Certificate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.henriquemgomes.cmpconverter.Utils;
import com.henriquemgomes.cmpconverter.dtos.CreateMessageDto;
import com.henriquemgomes.cmpconverter.enums.PKIBodyOptions;
import com.henriquemgomes.cmpconverter.exceptions.CmpConverterException;
import com.henriquemgomes.cmpconverter.interfaces.ConversionInterface;
import com.henriquemgomes.cmpconverter.models.CertRequestModel;
import com.henriquemgomes.cmpconverter.models.ExtraCertsModel;
import com.henriquemgomes.cmpconverter.models.PKIBodyModel;
import com.henriquemgomes.cmpconverter.models.PKIHeaderModel;

import jakarta.validation.Valid;

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
            case cp:
                return conversionServices.get("certRepMessagesConversionService");
            default:
                System.out.println("Type not supported");
                return null;
        }
    }

    public byte[] convertToCmp(CreateMessageDto createMessageDto) throws Exception {
        PKIHeader pkiHeader = pkiHeaderConversionService.createPkiHeader(createMessageDto);
        
        BodyConverterInterface conversionService = this.getDynamicConversionService(createMessageDto.getType());
        byte[] bodyContent = conversionService.convertToCmp(createMessageDto);
        PKIBody pkiBody = new PKIBody(conversionService.getType(), conversionService.getEncodable(bodyContent));

        CMPCertificate[] extraCerts = this.generateEncodedExtraCerts(createMessageDto.getExtraCerts());

        PKIMessage pkiMessage = new PKIMessage(pkiHeader, pkiBody, null, extraCerts);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
		pkiMessage.toASN1Primitive().encodeTo(bos);
		return bos.toByteArray();
    }

    private CMPCertificate[] generateEncodedExtraCerts(List<ExtraCertsModel> extraCerts) {
        List<CMPCertificate> encodedExtraCerts = new ArrayList<>();

        for (ExtraCertsModel extraCertsModel : extraCerts) {
            Certificate cert = Certificate.getInstance(Base64.decodeBase64(extraCertsModel.getContent()));
            CMPCertificate extraCert = new CMPCertificate(cert);
            encodedExtraCerts.add(extraCert);
        }

        return encodedExtraCerts.toArray(new CMPCertificate[] {});
    }

    public CreateMessageDto convertToJson(PKIMessage pkiMessage) throws CmpConverterException, IOException, ParseException {
        PKIBody pkiBody = pkiMessage.getBody();
        PKIBodyOptions type = PKIBodyOptions.valueOf(Utils.translateCMPMessageType(pkiBody.getType()));
        BodyConverterInterface conversionService = this.getDynamicConversionService(type);

        PKIHeaderModel pkiHeaderModel = new PKIHeaderModel(pkiMessage.getHeader());

        PKIBodyModel pkiBodyModel = conversionService.createBodyModel(pkiBody);

        List<ExtraCertsModel> extraCertsList = new ArrayList<>();
        if(pkiMessage.getExtraCerts() != null) {
            for (CMPCertificate extraCert : pkiMessage.getExtraCerts()) {
                ExtraCertsModel extraCertsModel = new ExtraCertsModel();
                extraCertsModel.setContent(Base64.encodeBase64String(extraCert.getEncoded()));
                extraCertsList.add(extraCertsModel);
            }
        }

        CreateMessageDto createMessageDto = new CreateMessageDto(type, pkiHeaderModel, extraCertsList);
        createMessageDto.setBody(pkiBodyModel);

        return createMessageDto;
    }

    public byte[] generateCertReq(@Valid CertRequestModel certReqModel) {
        return null;
    }
 }
