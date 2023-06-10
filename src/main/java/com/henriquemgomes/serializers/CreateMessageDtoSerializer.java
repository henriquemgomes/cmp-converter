package com.henriquemgomes.serializers;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.henriquemgomes.cmpconverter.dtos.CreateMessageDto;

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
        jgen.writeObjectField("header", value.getHeader());
        switch (value.getType()) {
            case cr:           
                jgen.writeObjectField("body", value.getCertificationRequest());
                break;
            case cp:
                jgen.writeObjectField("body", value.getCertRepMessage());
                break;
            default:
                //TODO exception
                System.out.println("serializer error");
                break;
        }
        jgen.writeObjectField("extra_certs", value.getExtraCerts());
        jgen.writeEndObject();
        
    }
}
