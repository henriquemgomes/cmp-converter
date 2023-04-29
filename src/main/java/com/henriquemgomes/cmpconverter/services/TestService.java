package com.henriquemgomes.cmpconverter.services;

import org.bouncycastle.cert.cmp.ProtectedPKIMessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class TestService {

    public String test(String textToReturn) {
        ProtectedPKIMessageBuilder pkiMessageBuilder = new ProtectedPKIMessageBuilder(null, null);
        System.out.println(pkiMessageBuilder);
        return textToReturn;
    }

}
