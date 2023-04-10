package com.henriquemgomes.cmpconverter.services;

import org.springframework.stereotype.Service;

import com.henriquemgomes.cmpconverter.dtos.CreateMessageDto;
import com.henriquemgomes.cmpconverter.interfaces.ConversionInterface;

@Service
public class CertReqMessagesConversionService implements ConversionInterface {

    public Object convertToCmp(CreateMessageDto createMessageDto) {
        System.out.println("in svc");
        return null;
    }
}
