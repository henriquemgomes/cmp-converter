package com.henriquemgomes.cmpconverter.services;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.cmp.InfoTypeAndValue;
import org.bouncycastle.asn1.cmp.PKIHeader;
import org.bouncycastle.asn1.cmp.PKIHeaderBuilder;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNamesBuilder;
import org.springframework.stereotype.Service;

import com.henriquemgomes.cmpconverter.Utils;
import com.henriquemgomes.cmpconverter.dtos.CreateMessageDto;
import com.henriquemgomes.cmpconverter.models.PKIHeaderModel;

@Service
public class PKIHeaderConversionService {
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public PKIHeaderConversionService() {
        this.sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		this.sdf.setLenient(false);
    }

    
    public PKIHeader createPkiHeader(CreateMessageDto createMessageDto) throws Exception {
        PKIHeaderModel pkiHeaderModel = createMessageDto.getHeader();

        GeneralName sender = new GeneralName(new X500Name(Utils.generateDn(pkiHeaderModel.getSender())));
        GeneralName recipient = new GeneralName(new X500Name(Utils.generateDn(pkiHeaderModel.getRecipient())));

        
        PKIHeaderBuilder pkiHeaderBuilder = new PKIHeaderBuilder(2, sender, recipient);
       
        if (pkiHeaderModel.getMessageTime() == null) {
            ASN1GeneralizedTime generalizedTime = new ASN1GeneralizedTime(new Date());
            pkiHeaderBuilder.setMessageTime(generalizedTime);
        } else {
            Date messageTime = sdf.parse(pkiHeaderModel.getMessageTime());
            ASN1GeneralizedTime generalizedTime = new ASN1GeneralizedTime(messageTime);
            pkiHeaderBuilder.setMessageTime(generalizedTime);
        }

        if (pkiHeaderModel.getProtectionAlg() != null) {
            pkiHeaderBuilder.setProtectionAlg(new AlgorithmIdentifier(new ASN1ObjectIdentifier(pkiHeaderModel.getProtectionAlg())));
        }

        if (pkiHeaderModel.getSenderKID() != null) {
            if(pkiHeaderModel.getSenderKID().startsWith("#"))
                pkiHeaderBuilder.setSenderKID(Utils.decodeHexString(pkiHeaderModel.getSenderKID().substring(1)));
            else if(pkiHeaderModel.getSenderKID().startsWith("n"))
                pkiHeaderBuilder.setSenderKID(Utils.encodeBigInteger(new BigInteger(pkiHeaderModel.getSenderKID().substring(1))));
            else
                pkiHeaderBuilder.setSenderKID(pkiHeaderModel.getSenderKID().getBytes());
        }

        if (pkiHeaderModel.getRecipKID() != null) {
            if(pkiHeaderModel.getRecipKID().startsWith("#"))
                pkiHeaderBuilder.setRecipKID(Utils.decodeHexString(pkiHeaderModel.getRecipKID().substring(1)));
            else if(pkiHeaderModel.getRecipKID().startsWith("n"))
                pkiHeaderBuilder.setRecipKID(Utils.encodeBigInteger(new BigInteger(pkiHeaderModel.getRecipKID().substring(1))));
            else
                pkiHeaderBuilder.setRecipKID(pkiHeaderModel.getRecipKID().getBytes());
        }

        if (pkiHeaderModel.getTransactionID() != null) {
            pkiHeaderBuilder.setTransactionID(pkiHeaderModel.getTransactionID().getBytes());
        }

        if (pkiHeaderModel.getSenderNonce() != null) {
            if(pkiHeaderModel.getSenderNonce().startsWith("#"))
                pkiHeaderBuilder.setSenderNonce(Utils.decodeHexString(pkiHeaderModel.getSenderNonce().substring(1)));
            else if(pkiHeaderModel.getSenderNonce().startsWith("n"))
                pkiHeaderBuilder.setSenderNonce(Utils.encodeBigInteger(new BigInteger(pkiHeaderModel.getSenderNonce().substring(1))));
            else
                pkiHeaderBuilder.setSenderNonce(pkiHeaderModel.getSenderNonce().getBytes());
        }

        if (pkiHeaderModel.getRecipNonce() != null) {
            if(pkiHeaderModel.getRecipNonce().startsWith("#"))
                pkiHeaderBuilder.setRecipNonce(Utils.decodeHexString(pkiHeaderModel.getRecipNonce().substring(1)));
            else if(pkiHeaderModel.getRecipNonce().startsWith("n"))
                pkiHeaderBuilder.setRecipNonce(Utils.encodeBigInteger(new BigInteger(pkiHeaderModel.getRecipNonce().substring(1))));
            else
                pkiHeaderBuilder.setRecipNonce(pkiHeaderModel.getRecipNonce().getBytes());
        }

        if (pkiHeaderModel.getGeneralInfo() != null) {
            List<InfoTypeAndValue> infoTypeAndValueList = new ArrayList<>();
            if (pkiHeaderModel.getGeneralInfo().getImplicitConfirm() != null) {
                InfoTypeAndValue infoTypeAndValue = new InfoTypeAndValue(new ASN1ObjectIdentifier("1.3.6.1.5.5.7.4.13"));
                infoTypeAndValueList.add(infoTypeAndValue);
            }
            
            if (pkiHeaderModel.getGeneralInfo().getConfirmWaitTime() != null) {
                Date messageTime = sdf.parse(pkiHeaderModel.getGeneralInfo().getConfirmWaitTime().getValue());
                ASN1GeneralizedTime generalizedTime = new ASN1GeneralizedTime(messageTime);

                InfoTypeAndValue infoTypeAndValue = new InfoTypeAndValue(new ASN1ObjectIdentifier("1.3.6.1.5.5.7.4.14"), generalizedTime);
                infoTypeAndValueList.add(infoTypeAndValue);
            }
            pkiHeaderBuilder.setGeneralInfo(infoTypeAndValueList.toArray(new InfoTypeAndValue[] {}));
        }

        return pkiHeaderBuilder.build();
    }

}
