package com.henriquemgomes.cmpconverter.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import org.apache.tomcat.util.codec.binary.Base64;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.crmf.CertRequest;
import org.bouncycastle.asn1.crmf.CertTemplate;
import org.bouncycastle.asn1.crmf.CertTemplateBuilder;
import org.bouncycastle.asn1.crmf.Controls;
import org.bouncycastle.asn1.crmf.OptionalValidity;
import org.bouncycastle.asn1.crmf.AttributeTypeAndValue;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.Time;
import org.bouncycastle.cert.crmf.AuthenticatorControl;
import org.bouncycastle.cert.crmf.RegTokenControl;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.asn1.x509.GeneralName;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.henriquemgomes.cmpconverter.Utils;
import com.henriquemgomes.cmpconverter.dtos.SignCertRequestDto;
import com.henriquemgomes.cmpconverter.exceptions.CmpConverterException;
import com.henriquemgomes.cmpconverter.models.CertRequestModel;
import com.henriquemgomes.cmpconverter.models.CertTemplateModel;
import com.henriquemgomes.cmpconverter.models.ExtensionsModel;
import com.henriquemgomes.cmpconverter.models.ExtraCertsModel;
import com.henriquemgomes.cmpconverter.models.OptionalValidityModel;
import com.henriquemgomes.cmpconverter.models.SubjectAltNameModel;

@Service
public class CertRequestService {
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public CertRequestService() {
        this.sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		this.sdf.setLenient(false);
    }

    public byte[] generateCertRequest(CertRequestModel certRequestModel, List<ExtraCertsModel> extraCerts) throws Exception{
        CertTemplate certTemplate = this.createCertTemplate(certRequestModel.getCertTemplate(), extraCerts);
        List<AttributeTypeAndValue> controlsList = new ArrayList<>();

        if(certRequestModel.getControls().getAuthenticatorControl() != null) {
            AuthenticatorControl control = new AuthenticatorControl(certRequestModel.getControls().getAuthenticatorControl().getValue());
            controlsList.add(new AttributeTypeAndValue(control.getType(), control.getValue()));
        }

        if(certRequestModel.getControls().getRegTokenControl() != null) {
            RegTokenControl control = new RegTokenControl(certRequestModel.getControls().getRegTokenControl().getValue());
            controlsList.add(new AttributeTypeAndValue(control.getType(), control.getValue()));
        }

        AttributeTypeAndValue[] atvs = controlsList.toArray(new AttributeTypeAndValue[] {});
        Controls controls = new Controls(atvs);

        CertRequest certRequest = new CertRequest(certRequestModel.getCertReqId(), certTemplate, controls);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
		certRequest.toASN1Primitive().encodeTo(bos);
		return bos.toByteArray();
    }

    public CertTemplate createCertTemplate (CertTemplateModel certTemplate, List<ExtraCertsModel> extraCerts) throws Exception {
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

    public HashMap<String, String> signCertRequest(SignCertRequestDto signCertRequestDto) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, OperatorCreationException {
        PrivateKey pvt = Utils.instantiatePrivateKeyFromB64(signCertRequestDto.getPrivKey());
        Security.addProvider(new BouncyCastleProvider());
		Provider prov = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME);
        ContentSigner signer = new JcaContentSignerBuilder("sha256withrsa").setProvider(prov).build(pvt);

        CertRequest certRequest = CertRequest.getInstance(Base64.decodeBase64(signCertRequestDto.getCertRequest()));
        byte[] contentToSign = certRequest.getEncoded();

        OutputStream sOut = signer.getOutputStream();
        
		sOut.write(contentToSign);

        sOut.close();

        HashMap<String, String> result = new HashMap<>();
        result.put("cert_request_signature", Base64.encodeBase64String(signer.getSignature()));
        return result;
    }
}
