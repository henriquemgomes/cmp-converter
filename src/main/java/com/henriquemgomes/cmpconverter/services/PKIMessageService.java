package com.henriquemgomes.cmpconverter.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;

import org.apache.tomcat.util.codec.binary.Base64;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cmp.PKIMessage;
import org.bouncycastle.asn1.crmf.CertReqMessages;
import org.bouncycastle.asn1.crmf.CertReqMsg;
import org.bouncycastle.asn1.crmf.CertRequest;
import org.bouncycastle.cert.cmp.CMPException;
import org.bouncycastle.cert.cmp.GeneralPKIMessage;
import org.bouncycastle.cert.cmp.ProtectedPKIMessage;
import org.bouncycastle.cert.cmp.ProtectedPKIMessageBuilder;
import org.bouncycastle.cert.crmf.CertificateRequestMessage;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;
import org.springframework.stereotype.Service;

import com.henriquemgomes.cmpconverter.Utils;
import com.henriquemgomes.cmpconverter.dtos.AttatchPKIMessageSignatureDto;
import com.henriquemgomes.cmpconverter.dtos.VerifySignatureDto;
import com.henriquemgomes.cmpconverter.exceptions.CmpConverterException;

@Service
public class PKIMessageService {

    public PKIMessageService() {
    }

    public byte[] attatchSignature(AttatchPKIMessageSignatureDto attatchPKIMessageSignatureDto) throws IOException {
        PKIMessage pkiMessage = PKIMessage.getInstance(Base64.decodeBase64(attatchPKIMessageSignatureDto.getPkiMessage()));
        DERBitString signature = new DERBitString(Base64.decodeBase64(attatchPKIMessageSignatureDto.getSignature()));

        PKIMessage protectedPKIMessage = new PKIMessage(pkiMessage.getHeader(), pkiMessage.getBody(), signature, pkiMessage.getExtraCerts());
        
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
		protectedPKIMessage.toASN1Primitive().encodeTo(bos);
		return bos.toByteArray();
    }

    public HashMap<String, Boolean> verifySignature(VerifySignatureDto verifySignatureDto) throws OperatorCreationException, CmpConverterException, CMPException, IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        PKIMessage pkiMessage = PKIMessage.getInstance(ASN1Primitive.fromByteArray(Base64.decodeBase64(verifySignatureDto.getPkiMessage())));
        GeneralPKIMessage generalPKIMessage = new GeneralPKIMessage(pkiMessage);
        ProtectedPKIMessage protectedPkiMessage = new ProtectedPKIMessage(generalPKIMessage);
        JcaContentVerifierProviderBuilder providerBuilder = new JcaContentVerifierProviderBuilder();
        ContentVerifierProvider contentVerifierProvider = providerBuilder.build(Utils.instantiatePublicKeyFromB64(verifySignatureDto.getPublicKey()));

        ASN1EncodableVector v = new ASN1EncodableVector();

        v.add(pkiMessage.getHeader());
        v.add(pkiMessage.getBody());

        System.out.println(Base64.encodeBase64String(new DERSequence(v).getEncoded(ASN1Encoding.DER)));
        System.out.println();
        System.out.println(Base64.encodeBase64String(pkiMessage.getProtection().getBytes()));
        HashMap<String, Boolean> result = new HashMap<>();
        result.put("valid_pki_message_signature", protectedPkiMessage.verify(contentVerifierProvider));
        return result;
    }

    public HashMap<String, String> sign(byte[] cmp, PrivateKey pvt) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException, IOException, OperatorCreationException {
        Security.addProvider(new BouncyCastleProvider());
		Provider prov = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME);
        ContentSigner msgsigner = new JcaContentSignerBuilder("sha256withrsa").setProvider(prov).build(pvt);

        PKIMessage pkiMessage = PKIMessage.getInstance(cmp);

        ASN1EncodableVector v = new ASN1EncodableVector();

        v.add(pkiMessage.getHeader());
        v.add(pkiMessage.getBody());

        OutputStream sOut = msgsigner.getOutputStream();
        
		sOut.write(new DERSequence(v).getEncoded(ASN1Encoding.DER));

        sOut.close();

        HashMap<String, String> result = new HashMap<>();
        result.put("pki_message_signature", Base64.encodeBase64String(msgsigner.getSignature()));
        return result;
	}

    public byte[] generateContentToSign(String pkiMessageB64) throws IOException {
        PKIMessage pkiMessage = PKIMessage.getInstance(Base64.decodeBase64(pkiMessageB64));

        ASN1EncodableVector v = new ASN1EncodableVector();

        v.add(pkiMessage.getHeader());
        v.add(pkiMessage.getBody());

        return new DERSequence(v).getEncoded(ASN1Encoding.DER);
    }

    public byte[] getCertRequest(String pkiMessage) throws IOException {
        PKIMessage pkiM = PKIMessage.getInstance(Base64.decodeBase64(pkiMessage));
        if(pkiM.getBody().getType() == 2) {
            CertReqMessages certReqMessages = CertReqMessages.getInstance(pkiM.getBody().getContent());
            CertReqMsg certReqMsg = certReqMessages.toCertReqMsgArray()[0];
            return certReqMsg.getCertReq().toASN1Primitive().getEncoded();
        } else {
            return null;
        }
    }

    public void ejbca (String pkiMessage) throws Exception {
        PKIMessage pkim = PKIMessage.getInstance(Base64.decodeBase64(pkiMessage));
        CertReqMessages certReqMessages = CertReqMessages.getInstance(pkim.getBody().getContent());
        CertReqMsg certReqMsg = CertReqMsg.getInstance(certReqMessages.getEncoded());

        DERSequence seq = (DERSequence) certReqMsg.toASN1Primitive();
        ASN1Encodable o2 = seq.getObjectAt(0);
       System.out.println(Base64.encodeBase64String(seq.getEncoded()));
        // ASN1Encodable o3 = ((DERSequence) o2).getObjectAt(0);
        // CertRequest cr = CertRequest.getInstance(o3);
        
        System.out.println("test");
    }
    
}
