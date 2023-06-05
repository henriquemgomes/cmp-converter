package com.henriquemgomes.cmpconverter.controllers;


import java.util.Map;

import org.apache.tomcat.util.codec.binary.Base64;
import org.bouncycastle.asn1.cmp.PKIMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.henriquemgomes.cmpconverter.dtos.AttatchPKIMessageSignatureDto;
import com.henriquemgomes.cmpconverter.dtos.CreateCertTemplateDto;
import com.henriquemgomes.cmpconverter.dtos.CreateMessageDto;
import com.henriquemgomes.cmpconverter.enums.PKIBodyOptions;
import com.henriquemgomes.cmpconverter.interfaces.ConversionInterface;
import com.henriquemgomes.cmpconverter.services.CertRequestService;
import com.henriquemgomes.cmpconverter.services.PKIMessageConversionService;
import com.henriquemgomes.cmpconverter.services.PKIMessageService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("convert")
@Validated
public class ConversionController {
    
    @Autowired
    private PKIMessageConversionService pkiMessageConversionService;

    @Autowired
    private CertRequestService certRequestService;

    @Autowired
    private PKIMessageService pkiMessageService;

    @PostMapping(consumes = {"application/json"}, produces = {"application/pkixcmp"})
    public ResponseEntity<Object> convertToCMP(@RequestBody @Valid CreateMessageDto createMessageDto) throws Exception {
        byte[] cmp = (byte[]) pkiMessageConversionService.convertToCmp(createMessageDto);
        return new ResponseEntity<>(cmp, HttpStatus.OK);
    }

    @PostMapping(consumes = {"application/pkixcmp"}, produces = {"application/json"})
    public ResponseEntity<Object> convertToJson(@RequestBody byte[] pkiMessageContent) throws Exception {
        PKIMessage pkiMessage = PKIMessage.getInstance(pkiMessageContent);
        CreateMessageDto createMessageDto = pkiMessageConversionService.convertToJson(pkiMessage);
        return new ResponseEntity<>(createMessageDto, HttpStatus.OK);
    } 

    @PostMapping(value = "generate-cert-template", consumes = {"application/json"}, produces = {"application/pkixcmp"})
    public ResponseEntity<Object> generateCertReq(@RequestBody @Valid CreateCertTemplateDto createCertTemplateDto) throws Exception {
        byte[] certTemplate = certRequestService.createCertTemplate(createCertTemplateDto.getCertTemplate(), createCertTemplateDto.getExtraCerts()).getEncoded();
        return new ResponseEntity<>(certTemplate, HttpStatus.OK);
    }

    @PostMapping(value = "pki-message/attatch-signature", consumes = {"application/json"}, produces = {"text/plain"})
    public ResponseEntity<Object> attatchSignature(@RequestBody @Valid AttatchPKIMessageSignatureDto attatchPKIMessageSignatureDto) throws Exception {
        byte[] protectedPKIMessage = (byte[]) pkiMessageService.attatchSignature(attatchPKIMessageSignatureDto);
        return new ResponseEntity<>(protectedPKIMessage, HttpStatus.OK);
    }
}
