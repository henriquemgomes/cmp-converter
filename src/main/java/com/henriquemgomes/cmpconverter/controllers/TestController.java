package com.henriquemgomes.cmpconverter.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.henriquemgomes.cmpconverter.services.TestService;

@RestController
@RequestMapping("test")
public class TestController {
    
    @Autowired
    private TestService testService;

    @GetMapping()
    public String test() {
        return this.testService.test("OK!");
    }
}
