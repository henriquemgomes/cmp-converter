package com.henriquemgomes.cmpconverter.controllers;


import java.util.Map;

import org.bouncycastle.asn1.cmp.PKIMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.henriquemgomes.cmpconverter.dtos.CreateMessageDto;
import com.henriquemgomes.cmpconverter.enums.PKIBodyOptions;
import com.henriquemgomes.cmpconverter.interfaces.ConversionInterface;
import com.henriquemgomes.cmpconverter.services.PKIMessageConversionService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("convert")
@Validated
public class ConversionController {
    
    @Autowired
    private PKIMessageConversionService pkiMessageConversionService;

    @PostMapping(consumes = {"application/json"}, produces = {"application/pkixcmp"})
    public ResponseEntity<Object> convertToCMP(@RequestBody @Valid CreateMessageDto createMessageDto) throws Exception {
        byte[] cmp = (byte[]) pkiMessageConversionService.convertToCmp(createMessageDto);
        return new ResponseEntity<>(cmp, HttpStatus.OK);
        // conversionService.convertToCmp(createMessageDto);
        // return new ResponseEntity<>(createMessageDto, HttpStatus.OK);
    }

    @PostMapping(consumes = {"application/pkixcmp"}, produces = {"application/json"})
    public ResponseEntity<Object> convertToJson(@RequestBody byte[] pkiMessageContent) throws Exception {
        PKIMessage pkiMessage = PKIMessage.getInstance(pkiMessageContent);
        CreateMessageDto createMessageDto = pkiMessageConversionService.convertToJson(pkiMessage);
        return new ResponseEntity<>(createMessageDto, HttpStatus.OK);
    }
}
