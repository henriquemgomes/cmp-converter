package com.henriquemgomes.cmpconverter.models;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.cmp.InfoTypeAndValue;
import org.bouncycastle.asn1.cmp.PKIHeader;
import org.bouncycastle.asn1.x500.X500Name;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.henriquemgomes.cmpconverter.Utils;

import jakarta.validation.constraints.NotNull;

public class PKIHeaderModel {

    /*
        PKIHeader ::= SEQUENCE {
            pvno                INTEGER     { cmp1999(1), cmp2000(2) },
            sender              GeneralName,
            recipient           GeneralName,
            messageTime     [0] GeneralizedTime         OPTIONAL,
            protectionAlg   [1] AlgorithmIdentifier     OPTIONAL,
            senderKID       [2] KeyIdentifier           OPTIONAL,
            recipKID        [3] KeyIdentifier           OPTIONAL,
            transactionID   [4] OCTET STRING            OPTIONAL,
            senderNonce     [5] OCTET STRING            OPTIONAL,
            recipNonce      [6] OCTET STRING            OPTIONAL,
            freeText        [7] PKIFreeText             OPTIONAL,
            generalInfo     [8] SEQUENCE SIZE (1..MAX) OF
                                InfoTypeAndValue     OPTIONAL
        }
        PKIFreeText ::= SEQUENCE SIZE (1..MAX) OF UTF8String
    */
    
    @NotNull(message = "header.sender is required.")
    private DistinguishedNameModel sender;

    @NotNull(message = "header.recipient is required.")
    private DistinguishedNameModel recipient;

    @JsonProperty("message_time")
    private String messageTime;

    @JsonProperty("protection_alg")
    private String protectionAlg;

    @JsonProperty("sender_kid")
    private String senderKID;

    @JsonProperty("recip_kid")
    private String recipKID;

    @JsonProperty("transaction_id")
    private String transactionID;

    @JsonProperty("sender_nonce")
    private String senderNonce;

    @JsonProperty("recip_nonce")
    private String recipNonce;

    @JsonProperty("general_info")
    private InfoTypeAndValueModel generalInfo;

    public PKIHeaderModel() {
    }

    public PKIHeaderModel(PKIHeader pkiHeader) throws IOException, ParseException {
        if (pkiHeader.getSender() != null) {
            this.sender = Utils.parseDn(X500Name.getInstance(pkiHeader.getSender().getName()).getRDNs());
        }

        if (pkiHeader.getRecipient() != null) {
            this.recipient = Utils.parseDn(X500Name.getInstance(pkiHeader.getRecipient().getName()).getRDNs());
        }

        if (pkiHeader.getMessageTime() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            sdf.setLenient(false);
            this.messageTime = sdf.format(pkiHeader.getMessageTime().getDate());
        }

        if (pkiHeader.getProtectionAlg() != null) {
            this.protectionAlg = pkiHeader.getProtectionAlg().getAlgorithm().getId();
        }

        if (pkiHeader.getSenderKID() != null) {
            this.senderKID = Utils.encodeToHexString(pkiHeader.getSenderKID().getOctets());
        }

        if (pkiHeader.getRecipKID() != null) {
            this.recipKID = Utils.encodeToHexString(pkiHeader.getRecipKID().getOctets());
        }

        if (pkiHeader.getTransactionID() != null) {
            this.transactionID = Utils.decodeHexBytesToString(pkiHeader.getTransactionID().getOctets());
        }

        if (pkiHeader.getSenderNonce() != null) {
            this.senderNonce = Utils.encodeToHexString(pkiHeader.getSenderNonce().getOctets());
        }

        if (pkiHeader.getRecipNonce() != null) {
            this.recipNonce = Utils.encodeToHexString(pkiHeader.getRecipNonce().getOctets());
        }

        if (pkiHeader.getGeneralInfo() != null) {
            this.generalInfo = new InfoTypeAndValueModel();
            InfoTypeAndValue[] generalInfo = pkiHeader.getGeneralInfo();

            for (InfoTypeAndValue infoTypeAndValue : generalInfo) {
                if(infoTypeAndValue.getInfoType().toString().equals("1.3.6.1.5.5.7.4.13")) {
                    BasicInfoTypeAndValueModel implicitConfirm = new BasicInfoTypeAndValueModel();
                    implicitConfirm.setValue(null);
                    this.generalInfo.setImplicitConfirm(implicitConfirm);
                }
                if(infoTypeAndValue.getInfoType().toString().equals("1.3.6.1.5.5.7.4.14")) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                    sdf.setLenient(false);

                    BasicInfoTypeAndValueModel confirmWaitTime = new BasicInfoTypeAndValueModel();
                    ASN1GeneralizedTime generalizedTime = ASN1GeneralizedTime.getInstance(infoTypeAndValue.getInfoValue());
                    confirmWaitTime.setValue(sdf.format(generalizedTime.getDate()));
                    this.generalInfo.setConfirmWaitTime(confirmWaitTime);
                }
            }
        }
    }

    public DistinguishedNameModel getSender() {
        return this.sender;
    }

    public void setSender(DistinguishedNameModel sender) {
        this.sender = sender;
    }

    public DistinguishedNameModel getRecipient() {
        return this.recipient;
    }

    public void setRecipient(DistinguishedNameModel recipient) {
        this.recipient = recipient;
    }

    public String getMessageTime() {
        return this.messageTime;
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }

    public String getProtectionAlg() {
        return this.protectionAlg;
    }

    public void setProtectionAlg(String protectionAlg) {
        this.protectionAlg = protectionAlg;
    }

    public String getSenderKID() {
        return this.senderKID;
    }

    public void setSenderKID(String senderKID) {
        this.senderKID = senderKID;
    }

    public String getRecipKID() {
        return this.recipKID;
    }

    public void setRecipKID(String recipKID) {
        this.recipKID = recipKID;
    }

    public String getTransactionID() {
        return this.transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public String getSenderNonce() {
        return this.senderNonce;
    }

    public void setSenderNonce(String senderNonce) {
        this.senderNonce = senderNonce;
    }

    public String getRecipNonce() {
        return this.recipNonce;
    }

    public void setRecipNonce(String recipNonce) {
        this.recipNonce = recipNonce;
    }

    public InfoTypeAndValueModel getGeneralInfo() {
        return this.generalInfo;
    }

    public void setGeneralInfo(InfoTypeAndValueModel generalInfo) {
        this.generalInfo = generalInfo;
    }
}
