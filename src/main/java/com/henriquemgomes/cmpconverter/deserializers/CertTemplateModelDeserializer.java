package com.henriquemgomes.cmpconverter.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.henriquemgomes.cmpconverter.models.CertTemplateModel;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import org.apache.tomcat.util.codec.binary.Base64;
import org.bouncycastle.asn1.crmf.CertTemplate;

@Slf4j
public class CertTemplateModelDeserializer extends JsonDeserializer<CertTemplateModel> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public CertTemplateModel deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        if (node != null) {
            if(node.isObject()) {
                return objectMapper.readValue(node.toString(), CertTemplateModel.class);
            } else if (node.isTextual()) {
                log.info("Instantiating cert template from b64.");
                return new CertTemplateModel(CertTemplate.getInstance(Base64.decodeBase64(node.asText())));
            }
        }

        return null;
    }
}
