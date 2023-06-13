package com.henriquemgomes.cmpconverter.serializers;

import java.io.IOException;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.henriquemgomes.cmpconverter.dtos.CreateMessageDto;
import com.henriquemgomes.cmpconverter.exceptions.CmpConverterException;

public class CreateMessageDtoSerializer extends StdSerializer<CreateMessageDto> {
    
    public CreateMessageDtoSerializer() {
        this(null);
    }
  
    public CreateMessageDtoSerializer(Class<CreateMessageDto> t) {
        super(t);
    }

    @Override
    public void serialize(
      CreateMessageDto value, JsonGenerator jgen, SerializerProvider provider) 
      throws IOException, JsonProcessingException {
        jgen.writeStartObject();
        jgen.writeObjectField("type", value.getType().type);
        jgen.writeObjectField("header", value.getHeader());
        switch (value.getType()) {
            case cr:           
                jgen.writeObjectField("body", value.getCertificationRequest());
                break;
            case cp:
                jgen.writeObjectField("body", value.getCertRepMessage());
                break;
            case rr:
                jgen.writeObjectField("body", value.getRevReqContentModel());
                break;
            case rp:
                jgen.writeObjectField("body", value.getRevRepContentModel());
                break;
            default:
                throw new IOException("Serialization error. Unsupported body type.");
        }
        jgen.writeObjectField("extra_certs", value.getExtraCerts());
        jgen.writeEndObject();
        
    }
}
