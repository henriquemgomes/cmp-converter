package com.henriquemgomes.cmpconverter.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import lombok.Data;

@Data
public class ExtensionsModel {
    @JsonProperty("subject_alt_names")
    @Valid
    private List<SubjectAltNameModel> subjectAltNames;
}
