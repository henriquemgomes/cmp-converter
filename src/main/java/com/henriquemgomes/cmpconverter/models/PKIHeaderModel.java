package com.henriquemgomes.cmpconverter.models;

import java.util.List;

import org.bouncycastle.asn1.cmp.InfoTypeAndValue;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
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
