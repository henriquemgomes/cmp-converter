package com.henriquemgomes.cmpconverter.deserializers;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.henriquemgomes.cmpconverter.dtos.CreateMessageDto;
import com.henriquemgomes.cmpconverter.enums.PKIBodyOptions;
import com.henriquemgomes.cmpconverter.models.CertificationRequestModel;
import com.henriquemgomes.cmpconverter.models.ExtraCertsModel;
import com.henriquemgomes.cmpconverter.models.PKIHeaderModel;
import com.henriquemgomes.cmpconverter.models.RevReqContentModel;

public class CreateMessageDtoDeserializer extends JsonDeserializer<CreateMessageDto> {
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public CreateMessageDto deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        String type = node.get("type").asText();
        PKIHeaderModel pkiHeaderModel = objectMapper.readValue(node.get("header").toString(), PKIHeaderModel.class);

        List<ExtraCertsModel> extraCerts = objectMapper.readValue(node.get("extra_certs").toString(), new TypeReference<List<ExtraCertsModel>>() {});
    
        CreateMessageDto createMessageDto = new CreateMessageDto(PKIBodyOptions.valueOf(type), pkiHeaderModel, extraCerts);
        String body = node.get("body").toString();
        switch (type) {
            case "cr":
                createMessageDto.setBody(objectMapper.readValue(body, CertificationRequestModel.class));
                break;
            case "rr":
                createMessageDto.setBody(objectMapper.readValue(body, RevReqContentModel.class));
                break;
            default:
                break;
        }
        return createMessageDto;
        

    }
}
