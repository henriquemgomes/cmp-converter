package com.henriquemgomes.cmpconverter.interfaces;

import com.henriquemgomes.cmpconverter.dtos.CreateMessageDto;
import com.henriquemgomes.cmpconverter.models.PKIMessageModel;

public interface ConversionInterface {
    byte[] convertToCmp(CreateMessageDto createMessageDto) throws Exception;
}