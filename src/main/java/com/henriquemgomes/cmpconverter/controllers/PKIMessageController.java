package com.henriquemgomes.cmpconverter.controllers;

import java.util.HashMap;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.henriquemgomes.cmpconverter.Utils;
import com.henriquemgomes.cmpconverter.dtos.AttatchPKIMessageSignatureDto;
import com.henriquemgomes.cmpconverter.dtos.GenerateContentToSignDto;
import com.henriquemgomes.cmpconverter.dtos.SignatureDto;
import com.henriquemgomes.cmpconverter.dtos.VerifySignatureDto;
import com.henriquemgomes.cmpconverter.services.PKIMessageService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("pki-message")
public class PKIMessageController {

    @Autowired
    private PKIMessageService pkiMessageService;
    
    @PostMapping(value = "generate-content-to-sign", consumes = {"application/json"}, produces = {"application/pkixcmp"})
    public ResponseEntity<Object> generateContentToSign(@RequestBody @Valid GenerateContentToSignDto generateContentToSignDto) throws Exception {
        byte[] contentToSign = pkiMessageService.generateContentToSign(generateContentToSignDto.getPkiMessage());
        return new ResponseEntity<>(contentToSign, HttpStatus.OK);
    }

    @PostMapping(value = "cert-request", consumes = {"application/json"}, produces = {"application/pkixcmp"})
    public ResponseEntity<Object> getCertRequest(@RequestBody @Valid GenerateContentToSignDto generateContentToSignDto) throws Exception {
        byte[] contentToSign = pkiMessageService.getCertRequest(generateContentToSignDto.getPkiMessage());
        return new ResponseEntity<>(contentToSign, HttpStatus.OK);
    }

    @PostMapping(value = "attatch-signature", consumes = {"application/json"}, produces = {"text/plain"})
    public ResponseEntity<Object> attatchSignature(@RequestBody @Valid AttatchPKIMessageSignatureDto attatchPKIMessageSignatureDto) throws Exception {
        byte[] protectedPKIMessage = (byte[]) pkiMessageService.attatchSignature(attatchPKIMessageSignatureDto);
        return new ResponseEntity<>(protectedPKIMessage, HttpStatus.OK);
    }

    @PostMapping(value = "verify-signature", consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<Object> verifySignature(@RequestBody @Valid VerifySignatureDto verifyPKIMessageSignatureDto) throws Exception {
        HashMap<String, Boolean> result = pkiMessageService.verifySignature(verifyPKIMessageSignatureDto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping(value = "sign", consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<Object> sign(@RequestBody @Valid SignatureDto signatureDto) throws Exception {
        HashMap<String, String> result = pkiMessageService.sign(Base64.decodeBase64(signatureDto.getPkiMessage()), Utils.instantiatePrivateKeyFromB64(signatureDto.getPrivKey()));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
