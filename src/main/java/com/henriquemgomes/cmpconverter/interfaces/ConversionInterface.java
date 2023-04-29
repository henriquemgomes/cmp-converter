package com.henriquemgomes.cmpconverter.interfaces;

import com.henriquemgomes.cmpconverter.dtos.CreateMessageDto;
import com.henriquemgomes.cmpconverter.models.PKIMessageModel;

public interface ConversionInterface {
    Object convertToCmp(CreateMessageDto createMessageDto) throws Exception;
    // PKIMessageModel convertToJson(Object cmpMessage);
}