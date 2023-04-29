package com.henriquemgomes.cmpconverter.services;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.crmf.CertificateRequestMessage;
import org.bouncycastle.cert.crmf.CertificateRequestMessageBuilder;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.OtherName;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.henriquemgomes.cmpconverter.Utils;
import com.henriquemgomes.cmpconverter.dtos.CreateMessageDto;
import com.henriquemgomes.cmpconverter.exceptions.CmpConverterException;
import com.henriquemgomes.cmpconverter.interfaces.ConversionInterface;
import com.henriquemgomes.cmpconverter.models.CertReqMsgModel;
import com.henriquemgomes.cmpconverter.models.ExtensionsModel;
import com.henriquemgomes.cmpconverter.models.ExtraCertsModel;
import com.henriquemgomes.cmpconverter.models.OptionalValidityModel;
import com.henriquemgomes.cmpconverter.models.SubjectAltNameModel;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CertReqMessagesConversionService implements ConversionInterface {
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public CertReqMessagesConversionService() {
        this.sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		this.sdf.setLenient(false);
    }

    public byte[] convertToCmp(CreateMessageDto createMessageDto) throws Exception {
        log.info("Converting CertReqMessage to JSON.");
  
        CertificateRequestMessageBuilder messageBuilder = this.getMessageBuilder(createMessageDto);
        CertificateRequestMessage certificateRequestMessage = messageBuilder.build();
        log.info("CertReqMessage converted to JSON succesfully.");
        return certificateRequestMessage.getEncoded();
    }

    private CertificateRequestMessageBuilder getMessageBuilder(CreateMessageDto createMessageDto) throws Exception {
        log.info("Creating new CertificateRequestMessageBuilder containing given data.");
        CertificateRequestMessageBuilder messageBuilder = new CertificateRequestMessageBuilder(new BigInteger("3"));
        //TODO Verificar se pode ou se deve criar um novo campo para o certificado do emissor
        X500Name subjectDN = new X500Name(Utils.generateDn(
            createMessageDto.getCertificationRequest().getCertReqMessages().get(0).getCertReq().getCertTemplate().getSubject())
            );
        
        X500Name issuerDn = null;
        ExtraCertsModel issuerCert = createMessageDto.getExtraCerts().stream().filter(extraCert -> extraCert.getType().equals("issuer_cert")).findFirst().orElse(null);
        if(issuerCert != null) {
            Certificate recipientCert = Utils.getCertificateFromBase64(issuerCert.getContent()); 
            issuerDn = recipientCert.getSubject();
        }

        messageBuilder.setSubject(subjectDN);

        if(issuerDn != null) {
            messageBuilder.setIssuer(issuerDn);
        }

        if(createMessageDto.getCertificationRequest().getCertReqMessages().get(0).getCertReq().getCertTemplate().getPublicKey() != null){
            SubjectPublicKeyInfo subjectPublicKeyInfo = Utils.instantiatePublicKeyFromB64(createMessageDto.getCertificationRequest().getCertReqMessages().get(0).getCertReq().getCertTemplate().getPublicKey());
            messageBuilder.setPublicKey(subjectPublicKeyInfo);
        }

        OptionalValidityModel validity = createMessageDto.getCertificationRequest().getCertReqMessages().get(0).getCertReq().getCertTemplate().getValidity();

        Date notBefore = null;
        Date notAfter = null;

        if(validity != null) {
            if(validity.getNotBefore() != null) {
                try {
                    notBefore = sdf.parse(validity.getNotBefore());
                } catch (Exception e) {
                    throw new CmpConverterException(
                        "cmp.error.parse.validity.not_before",
                        "Failed to parse 'not_before' date. The required format is 'dd/MM/yyyy HH:mm:ss'. Timezone: UTC.",
                        500,
                        HttpStatus.UNPROCESSABLE_ENTITY,
                        null
                    );
                }
            }

            if(validity.getNotAfter() != null) {
                try {
                    notAfter = sdf.parse(validity.getNotAfter());
                } catch (Exception e) {
                    throw new CmpConverterException(
                        "cmp.error.parse.validity.not_after",
                        "Failed to parse 'not_after' date. The required format is 'dd/MM/yyyy HH:mm:ss'. Timezone: UTC.",
                        501,
                        HttpStatus.UNPROCESSABLE_ENTITY,
                        null
                    );
                }
            }

            if(notAfter != null || notBefore != null) {
                messageBuilder.setValidity(notBefore, notAfter);
            }
        }

        this.addExtensions(createMessageDto.getCertificationRequest().getCertReqMessages().get(0), messageBuilder);

        log.info("CertificateRequestMessageBuilder created succesfully.");
        return messageBuilder;
    }

    private void addExtensions(CertReqMsgModel certReqMsg, CertificateRequestMessageBuilder messageBuilder) throws CmpConverterException {
        ExtensionsModel extensions = certReqMsg.getCertReq().getCertTemplate().getExtensions();

        if(extensions.getSubjectAltNames() != null) {
            List<ASN1Encodable> encodableSubAltNameList = new ArrayList<>();
            for (SubjectAltNameModel subjectAltName : extensions.getSubjectAltNames()) {
                ASN1Encodable subjectAltNameContent = this.generateSubjectAltNameContent(subjectAltName);

                encodableSubAltNameList.add(subjectAltNameContent);
            }
            try {
                messageBuilder.addExtension(Extension.subjectAlternativeName, false, new DERSequence(encodableSubAltNameList.toArray(new ASN1Encodable[] {})));
            } catch (CertIOException e) {
                throw new CmpConverterException(
                        "converter.erro.add.extension.subAltName", "Failed to add subjectAltName extension: "+ e.getMessage(),
                        504,
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        null
                    );
            }
        }
    }

    private ASN1Encodable generateSubjectAltNameContent(SubjectAltNameModel subjectAltName) throws CmpConverterException {
        switch (subjectAltName.getGeneralName()) {
            case "otherName":
                ASN1ObjectIdentifier oid = null;

                try {
                    oid = new ASN1ObjectIdentifier(subjectAltName.getOid());
                } catch (Exception e) {
                    throw new CmpConverterException(
                        "converter.erro.converter.subAltName", "Failed to parse subAltName oid "+ subjectAltName.getOid() +": "+ e.getMessage(),
                        502,
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        null
                    );
                }

                byte[] value = null;

                try {
                    value = subjectAltName.getValue().getBytes();
                } catch (Exception e) {
                    throw new CmpConverterException(
                        "converter.erro.converter.subAltName", "Failed to parse subAltName value identified by oid "+ subjectAltName.getOid() +": "+ e.getMessage(),
                        503,
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        null
                    );
                }

                DEROctetString valueOctet = new DEROctetString(value);
                DERTaggedObject taggedValue = new DERTaggedObject(0, valueOctet);
                DERSequence sequence = new DERSequence(new ASN1Encodable[] {oid, taggedValue});
                GeneralName otherName = new GeneralName(GeneralName.otherName, sequence);
                return otherName;
            case "rfc822name":
                DERIA5String ia5string = new DERIA5String(subjectAltName.getValue());
                GeneralName rfc822name = new GeneralName(GeneralName.rfc822Name, ia5string);
                return rfc822name;
            default:
                return null;
        }
    }
    // TODO remover
    // throw new CmpConverterException("converter.erro.converter.cert.req.message", "Erro ao converter.", 100, HttpStatus.UNPROCESSABLE_ENTITY, null);

}
