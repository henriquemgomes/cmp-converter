package com.henriquemgomes.cmpconverter.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Enumerated;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.apache.tomcat.util.codec.binary.Base64;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.henriquemgomes.cmpconverter.Utils;

import jakarta.validation.Valid;
import lombok.Data;

@Data
public class ExtensionsModel {
    
    @JsonProperty("subject_alt_names")
    @Valid
    private List<SubjectAltNameModel> subjectAltNames;

    @JsonProperty("reason_code")
    @Valid
    private BasicExtensionModel reasonCode;

    public ExtensionsModel() {
    }

    public ExtensionsModel(Extensions extensions) throws IOException {
        ASN1ObjectIdentifier[] extOids = extensions.getExtensionOIDs();
        for (ASN1ObjectIdentifier oid : extOids) {
            if (oid.toString().equals(Extension.subjectAlternativeName.toString())) {
                List<SubjectAltNameModel> subjectAltNameModelList = new ArrayList<>();
                Extension extension = extensions.getExtension(oid);
                ASN1Sequence derSeq = DERSequence.getInstance(extension.getExtnValue().getOctets());
                Iterator<ASN1Encodable> it = derSeq.iterator();
                while (it.hasNext()) {
                    ASN1Encodable value = it.next();
                    ASN1TaggedObject taggedObj = DERTaggedObject.getInstance(value);
                    if(taggedObj.getTagNo() == 0) {
                        ASN1Sequence seq = DERSequence.getInstance(taggedObj, false);
                        String subAltNameOid = ASN1ObjectIdentifier.getInstance(seq.getObjectAt(0)).getId();
                        ASN1TaggedObject taggedSubaltNameVal = DERTaggedObject.getInstance(seq.getObjectAt(1));
                        String subAltNameValue = Utils.decodeHexBytesToString(DEROctetString.getInstance(taggedSubaltNameVal.getBaseObject()).getOctets());
                        subjectAltNameModelList.add(new SubjectAltNameModel(subAltNameOid, subAltNameValue, "otherName"));
                    }
                    
                    if(taggedObj.getTagNo() == 1) {
                        String subAltNameValue = Utils.decodeHexBytesToString(DEROctetString.getInstance(taggedObj.getBaseObject()).getOctets());
                        subjectAltNameModelList.add(new SubjectAltNameModel(null, subAltNameValue, "rfc822name"));
                    }
                }
                this.setSubjectAltNames(subjectAltNameModelList);
            }

            if(oid.toString().equals(Extension.reasonCode.toString())) {
                BasicExtensionModel reasonCodeExtensionModel = new BasicExtensionModel();
                Extension extension = extensions.getExtension(oid);
                DEROctetString value = new DEROctetString(extension.getExtnValue().getOctets());
                int reasonId = ASN1Enumerated.getInstance(value.getOctets()).getValue().intValue();
                reasonCodeExtensionModel.setValue(this.getReasonString(reasonId));
                this.reasonCode = reasonCodeExtensionModel;
            }
        }
    }

    private String getReasonString(int reasonId) {
        switch (reasonId) {
            case 0:
                return "unspecified";
            case 1:
                return "keyCompromise";
            case 2:
                return "cACompromise";
            case 3:
                return "affiliationChanged";
            case 4:
                return "superseded";
            case 5:
                return "cessationOfOperation";
            case 6:
                return "certificateHold";
            case 8:
                return "removeFromCRL";
            case 9:
                return "privilegeWithdrawn";
            case 10:
                return "aACompromise";
            default:
                return "unspecified";
        }
    }

}
