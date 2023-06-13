package com.henriquemgomes.cmpconverter.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.tomcat.util.codec.binary.Base64;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.cmp.PKIBody;
import org.bouncycastle.asn1.crmf.AttributeTypeAndValue;
import org.bouncycastle.asn1.crmf.CertReqMessages;
import org.bouncycastle.asn1.crmf.CertReqMsg;
import org.bouncycastle.asn1.crmf.CertRequest;
import org.bouncycastle.asn1.crmf.CertTemplate;
import org.bouncycastle.asn1.crmf.CertTemplateBuilder;
import org.bouncycastle.asn1.crmf.Controls;
import org.bouncycastle.asn1.crmf.OptionalValidity;
import org.bouncycastle.asn1.crmf.POPOSigningKey;
import org.bouncycastle.asn1.crmf.ProofOfPossession;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.Time;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.crmf.AuthenticatorControl;
import org.bouncycastle.cert.crmf.CertificateRequestMessage;
import org.bouncycastle.cert.crmf.CertificateRequestMessageBuilder;
import org.bouncycastle.cert.crmf.RegTokenControl;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;
import org.bouncycastle.asn1.x509.GeneralName;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.henriquemgomes.cmpconverter.Utils;
import com.henriquemgomes.cmpconverter.dtos.CreateMessageDto;
import com.henriquemgomes.cmpconverter.exceptions.CmpConverterException;
import com.henriquemgomes.cmpconverter.interfaces.BodyConverterInterface;
import com.henriquemgomes.cmpconverter.models.UTF8PairsModel;
import com.henriquemgomes.cmpconverter.models.CertReqMsgModel;
import com.henriquemgomes.cmpconverter.models.CertTemplateModel;
import com.henriquemgomes.cmpconverter.models.CertificationRequestModel;
import com.henriquemgomes.cmpconverter.models.ControlsModel;
import com.henriquemgomes.cmpconverter.models.ExtensionsModel;
import com.henriquemgomes.cmpconverter.models.ExtraCertsModel;
import com.henriquemgomes.cmpconverter.models.OptionalValidityModel;
import com.henriquemgomes.cmpconverter.models.PKIBodyModel;
import com.henriquemgomes.cmpconverter.models.ProofOfPossessionModel;
import com.henriquemgomes.cmpconverter.models.RegInfoModel;
import com.henriquemgomes.cmpconverter.models.SubjectAltNameModel;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CertReqMessagesConversionService implements BodyConverterInterface {
    
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public CertReqMessagesConversionService() {
        this.sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		this.sdf.setLenient(false);
    }

    public int getType() {
        return PKIBody.TYPE_CERT_REQ;
    }

    public ASN1Encodable getEncodable(byte[] content) {
        return CertReqMessages.getInstance(content);
    }

    public byte[] convertToCmp(CreateMessageDto createMessageDto) throws Exception {
        log.info("Converting CertReqMessage to CMP.");
  
        CertificateRequestMessageBuilder messageBuilder = this.getMessageBuilder(createMessageDto);
        CertificateRequestMessage certificateRequestMessage = messageBuilder.build();
        CertReqMsg buildedMessage = certificateRequestMessage.toASN1Structure();

        AttributeTypeAndValue[] regInfo = null;
        if(createMessageDto.getCertificationRequest().getCertReqMessages().get(0).getRegInfo() != null) {
            regInfo = this.getEncodedRegInfo(createMessageDto.getCertificationRequest().getCertReqMessages().get(0), createMessageDto.getExtraCerts());
        }

        ProofOfPossession popo = null;
        if(createMessageDto.getCertificationRequest().getCertReqMessages().get(0).getPopo() != null) {
            ProofOfPossessionModel popoModel = createMessageDto.getCertificationRequest().getCertReqMessages().get(0).getPopo();
            if(popoModel.getType().equals("popo_signing_key")) {
                ASN1BitString sig = new DERBitString(Base64.decodeBase64(popoModel.getValue()));
                POPOSigningKey poposk = new POPOSigningKey(null, new AlgorithmIdentifier(new ASN1ObjectIdentifier("1.2.840.113549.1.1.11")), sig);
                popo = new ProofOfPossession(poposk);
                buildedMessage = new CertReqMsg(buildedMessage.getCertReq(), popo, regInfo);
                if (!this.verifyPopoSigningKeySignature(buildedMessage)) {
                    throw new CmpConverterException(
                        "converter.error.popo.signature", 
                        "Invalid popo signature",
                        5055,
                        HttpStatus.BAD_REQUEST,
                        null
                    );
                }
            }
        } else {
            buildedMessage = new CertReqMsg(buildedMessage.getCertReq(), null, regInfo);
        }

        CertReqMessages certReqMessages = new CertReqMessages(buildedMessage);
        log.info("CertReqMessage converted to CMP succesfully.");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
		certReqMessages.toASN1Primitive().encodeTo(bos);
		return bos.toByteArray();
    }

    private CertificateRequestMessageBuilder getMessageBuilder(CreateMessageDto createMessageDto) throws Exception {
        log.info("Creating new CertificateRequestMessageBuilder containing given data.");
        CertificateRequestMessageBuilder messageBuilder = new CertificateRequestMessageBuilder(new BigInteger("3"));

        X500Name subjectDN = new X500Name(Utils.generateDn(
            createMessageDto.getCertificationRequest().getCertReqMessages().get(0).getCertReq().getCertTemplate().getSubject())
            );
        
        X500Name issuerDn = null;
        ExtraCertsModel issuerCert = createMessageDto.getExtraCerts().stream().filter(extraCert -> extraCert.getType().equals("issuer_cert")).findFirst().orElse(null);
        if(issuerCert != null) {
            Certificate decodedIssuerCert = Utils.getCertificateFromBase64(issuerCert.getContent()); 
            issuerDn = decodedIssuerCert.getSubject();
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
        this.addControls(createMessageDto.getCertificationRequest().getCertReqMessages().get(0), messageBuilder);

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
                        "converter.error.add.extension.subAltName", "Failed to add subjectAltName extension: "+ e.getMessage(),
                        504,
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        null
                    );
            }
        }
    }

    private void addControls(CertReqMsgModel certReqMsg, CertificateRequestMessageBuilder messageBuilder) throws CmpConverterException {
        ControlsModel controls = certReqMsg.getCertReq().getControls();

        if(controls != null) {
            if(controls.getAuthenticatorControl() != null) {
                AuthenticatorControl control = new AuthenticatorControl(controls.getAuthenticatorControl().getValue());
                messageBuilder.addControl(control);
            }
    
            if(controls.getRegTokenControl() != null) {
                RegTokenControl control = new RegTokenControl(controls.getRegTokenControl().getValue());
                messageBuilder.addControl(control);
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
                        "converter.error.converter.subAltName", "Failed to parse subAltName oid "+ subjectAltName.getOid() +": "+ e.getMessage(),
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
                        "converter.error.converter.subAltName", "Failed to parse subAltName value identified by oid "+ subjectAltName.getOid() +": "+ e.getMessage(),
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

    private AttributeTypeAndValue[] getEncodedRegInfo(CertReqMsgModel certReqMsg, List<ExtraCertsModel> extraCerts) throws Exception {
        RegInfoModel regInfo = certReqMsg.getRegInfo();
        ArrayList<AttributeTypeAndValue> encodedRegInfoList = new ArrayList<AttributeTypeAndValue>();
        AttributeTypeAndValue[] encodedRegInfoArray = {};

        if (regInfo.getUtf8pairs() != null && regInfo.getUtf8pairs().size() > 0) {
            for (UTF8PairsModel utf8Pairs : regInfo.getUtf8pairs()) {
                AttributeTypeAndValue encodedRegInfo = new AttributeTypeAndValue(new ASN1ObjectIdentifier("1.3.6.1.5.5.7.7.5.2.1"), 
                new DERTaggedObject(12, new DERUTF8String(utf8Pairs.getValue())));
                encodedRegInfoList.add(encodedRegInfo);
            }
        }

        if(regInfo.getCertReq() != null) {
            if(regInfo.getCertReq().getControls() != null) {
                CertTemplate certTemplate = this.createCertTemplate(regInfo.getCertReq().getCertTemplate(), extraCerts);
                List<AttributeTypeAndValue> controlsList = new ArrayList<>();
    
                if(regInfo.getCertReq().getControls().getAuthenticatorControl() != null) {
                    AuthenticatorControl control = new AuthenticatorControl(regInfo.getCertReq().getControls().getAuthenticatorControl().getValue());
                    controlsList.add(new AttributeTypeAndValue(control.getType(), control.getValue()));
                }
        
                if(regInfo.getCertReq().getControls().getRegTokenControl() != null) {
                    RegTokenControl control = new RegTokenControl(regInfo.getCertReq().getControls().getRegTokenControl().getValue());
                    controlsList.add(new AttributeTypeAndValue(control.getType(), control.getValue()));
                }
    
                Controls controls = new Controls(controlsList.toArray(new AttributeTypeAndValue[] {}));
    
                CertRequest certRequest = new CertRequest(regInfo.getCertReq().getCertReqId(), certTemplate, controls);
    
                AttributeTypeAndValue encodedRegInfo = new AttributeTypeAndValue(new ASN1ObjectIdentifier("1.3.6.1.5.5.7.7.5.2.2"), certRequest);
                encodedRegInfoList.add(encodedRegInfo);
            }
        }

        return encodedRegInfoList.toArray(encodedRegInfoArray);
    }

    private CertTemplate createCertTemplate (CertTemplateModel certTemplate, List<ExtraCertsModel> extraCerts) throws Exception {
        CertTemplateBuilder certTemplateBuilder = new CertTemplateBuilder();

        if(certTemplate.getVersion() != null) 
            certTemplateBuilder.setVersion(Integer.parseInt(certTemplate.getVersion()));

        if(certTemplate.getSerialNumber() != null)
        certTemplateBuilder.setSerialNumber(new ASN1Integer(certTemplate.getSerialNumber().getBytes()));
        // certTemplateBuilder.setSigningAlg();

        //Deprecated
        if(certTemplate.getIssuerUID() != null){
            if(certTemplate.getIssuerUID().startsWith("#"))
                certTemplateBuilder.setIssuerUID(new DERBitString(Utils.decodeHexString(certTemplate.getIssuerUID().substring(1))));
            else if(certTemplate.getIssuerUID().startsWith("n"))
                certTemplateBuilder.setIssuerUID(new DERBitString(Utils.encodeBigInteger(new BigInteger(certTemplate.getIssuerUID().substring(1)))));
            else
                certTemplateBuilder.setIssuerUID(new DERBitString(certTemplate.getIssuerUID().getBytes()));
        }

        //Deprecated
        if(certTemplate.getSubjectUID() != null){
            if(certTemplate.getSubjectUID().startsWith("#"))
                certTemplateBuilder.setSubjectUID(new DERBitString(Utils.decodeHexString(certTemplate.getSubjectUID().substring(1))));
            else if(certTemplate.getSubjectUID().startsWith("n"))
                certTemplateBuilder.setSubjectUID(new DERBitString(Utils.encodeBigInteger(new BigInteger(certTemplate.getSubjectUID().substring(1)))));
            else
                certTemplateBuilder.setSubjectUID(new DERBitString(certTemplate.getSubjectUID().getBytes()));
        }

        if(certTemplate.getSubject() != null)
            certTemplateBuilder.setSubject(new X500Name(Utils.generateDn(certTemplate.getSubject())));
        
        X500Name issuerDn = null;
        ExtraCertsModel issuerCert = extraCerts.stream().filter(extraCert -> extraCert.getType().equals("issuer_cert")).findFirst().orElse(null);
        if(issuerCert != null) {
            Certificate recipientCert = Utils.getCertificateFromBase64(issuerCert.getContent()); 
            issuerDn = recipientCert.getSubject();
        }

        if(issuerDn != null){
            certTemplateBuilder.setIssuer(issuerDn);
        }

        if(certTemplate.getPublicKey() != null){
            SubjectPublicKeyInfo subjectPublicKeyInfo = Utils.instantiatePublicKeyFromB64(certTemplate.getPublicKey());
            certTemplateBuilder.setPublicKey(subjectPublicKeyInfo);
        }

        OptionalValidityModel validity = certTemplate.getValidity();

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
                certTemplateBuilder.setValidity(new OptionalValidity(new Time(notBefore), new Time(notAfter)));
            }
        }

        if(certTemplate.getExtensions() != null) {
            ExtensionsModel extensionsModel = certTemplate.getExtensions();
            List<Extension> extensionList = new ArrayList<>();
    
            if (extensionsModel.getSubjectAltNames() != null) {
                List<ASN1Encodable> encodableSubAltNameList = new ArrayList<>();
                for (SubjectAltNameModel subjectAltName : extensionsModel.getSubjectAltNames()) {
                    ASN1Encodable subjectAltNameContent = this.generateSubjectAltNameContent(subjectAltName);
    
                    encodableSubAltNameList.add(subjectAltNameContent);
                }
                Extension subAltName = Extension.create(Extension.subjectAlternativeName, false, new DERSequence(encodableSubAltNameList.toArray(new ASN1Encodable[] {})));    
                extensionList.add(subAltName);
            }
            
            certTemplateBuilder.setExtensions(new Extensions(extensionList.toArray(new Extension[] {})));
        }

        return certTemplateBuilder.build();
    }

    private boolean verifyPopoSigningKeySignature(CertReqMsg certReqMsg) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, IOException, SignatureException {
        CertRequest protObject = certReqMsg.getCertReq();
		POPOSigningKey popSK = POPOSigningKey.getInstance(certReqMsg.getPopo().getObject());
		final ByteArrayOutputStream bao = new ByteArrayOutputStream();
		// new DEROutputStream(bao).writeObject(protObject);
        protObject.encodeTo(bao);
		final byte[] protBytes = bao.toByteArray();

		final AlgorithmIdentifier algId = popSK.getAlgorithmIdentifier();
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		final Signature sig = Signature.getInstance(algId.getAlgorithm().getId(), "BC");
		sig.initVerify(getPublicKey(certReqMsg.getCertReq().getCertTemplate().getPublicKey(),
				BouncyCastleProvider.PROVIDER_NAME));
		sig.update(protBytes);
		final ASN1BitString bs = popSK.getSignature();
		if (sig.verify(bs.getBytes())) {
			return true;
		}
		return false;
    }

    private PublicKey getPublicKey(final SubjectPublicKeyInfo subjectPKInfo, final String provider)
			throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException {
		// If there is no public key here, but only an empty bit string, it means we
		// have called for server generated keys
		// i.e. no public key to see here...
		if (subjectPKInfo.getPublicKeyData().equals(DERNull.INSTANCE)) {
			return null;
		}
		try {
			final X509EncodedKeySpec xspec = new X509EncodedKeySpec(new DERBitString(subjectPKInfo).getBytes());
			final AlgorithmIdentifier keyAlg = subjectPKInfo.getAlgorithm();
			return KeyFactory.getInstance(keyAlg.getAlgorithm().getId(), provider).generatePublic(xspec);
		} catch (InvalidKeySpecException | IOException e) {
			final InvalidKeyException newe = new InvalidKeyException("Error decoding public key.");
			newe.initCause(e);
			throw newe;
		}
	}

    @Override
    public PKIBodyModel createBodyModel(PKIBody pkiBody) throws CmpConverterException {
        CertificationRequestModel pkiBodyModel = new CertificationRequestModel();
        CertReqMessages certReqMessages = CertReqMessages.getInstance(pkiBody.getContent());
        
        pkiBodyModel.instantiate(certReqMessages);

        return pkiBodyModel;
    }

}

