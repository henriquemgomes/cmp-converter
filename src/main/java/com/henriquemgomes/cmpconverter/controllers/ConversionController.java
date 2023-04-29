package com.henriquemgomes.cmpconverter.controllers;


import java.util.Map;

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

import jakarta.validation.Valid;


@RestController
@RequestMapping("convert")
@Validated
public class ConversionController {
    
    @Autowired
    private Map<String, ConversionInterface> conversionServices;

    private ConversionInterface getDynamicConversionService(PKIBodyOptions messageType) {
        switch (messageType) {
            case ir:
            case cr:
                return conversionServices.get("certReqMessagesConversionService");
            default:
                System.out.println("Type not supported");
                return null;
        }
    } 

    @PostMapping(consumes = {"application/json"}, produces = {"application/pkixcmp"})
    public ResponseEntity<Object> convertToCMP(@RequestBody @Valid CreateMessageDto createMessageDto) throws Exception {
        ConversionInterface conversionService = this.getDynamicConversionService(createMessageDto.getType());
        byte[] cmp = (byte[]) conversionService.convertToCmp(createMessageDto);
        return new ResponseEntity<>(cmp, HttpStatus.OK);
        // conversionService.convertToCmp(createMessageDto);
        // return new ResponseEntity<>(createMessageDto, HttpStatus.OK);
    }
}
