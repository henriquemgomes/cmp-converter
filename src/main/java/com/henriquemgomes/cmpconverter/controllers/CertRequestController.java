package com.henriquemgomes.cmpconverter.controllers;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.henriquemgomes.cmpconverter.dtos.CreateCertTemplateDto;
import com.henriquemgomes.cmpconverter.dtos.SignCertRequestDto;
import com.henriquemgomes.cmpconverter.services.CertRequestService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("cert-request")
public class CertRequestController {

    @Autowired
    private CertRequestService certRequestService;
    
    @PostMapping(value = "generate-cert-template", consumes = {"application/json"}, produces = {"application/pkixcmp"})
    public ResponseEntity<Object> generateCertReq(@RequestBody @Valid CreateCertTemplateDto createCertTemplateDto) throws Exception {
        byte[] certTemplate = certRequestService.createCertTemplate(createCertTemplateDto.getCertTemplate(), createCertTemplateDto.getExtraCerts()).getEncoded();
        return new ResponseEntity<>(certTemplate, HttpStatus.OK);
    }

    @PostMapping(value = "sign-cert-request", consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<Object> signCertRequest(@RequestBody @Valid SignCertRequestDto signCertRequestDto) throws Exception {
        HashMap<String, String> result = certRequestService.signCertRequest(signCertRequestDto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
