package ru.covid.app.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record GenerateQrRequest(@JsonProperty("sheetId") String sheetId, @JsonProperty("userId") String userId) {

    @JsonCreator
    public GenerateQrRequest {
    }
}
