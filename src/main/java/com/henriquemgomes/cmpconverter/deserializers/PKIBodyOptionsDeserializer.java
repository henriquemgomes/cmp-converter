package com.henriquemgomes.cmpconverter.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.henriquemgomes.cmpconverter.enums.PKIBodyOptions;

import java.io.IOException;

public class PKIBodyOptionsDeserializer extends JsonDeserializer<PKIBodyOptions> {

    @Override
    public PKIBodyOptions deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        String enumValue = node.asText();

        for (PKIBodyOptions option : PKIBodyOptions.values()) {
            if (option.type.equals(enumValue)) {
                return option;
            }
        }

        throw new IllegalArgumentException("Unknown PKIBodyOptions value: " + enumValue);
    }
}
