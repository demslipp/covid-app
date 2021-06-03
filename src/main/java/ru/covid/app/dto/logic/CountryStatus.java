package ru.covid.app.dto.logic;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.covid.app.jooq.tables.records.CountryStatusRecord;

public record CountryStatus(
    @JsonProperty("name") String name,
    @JsonProperty("status") Status status,
    @JsonProperty("quarantineIn") Options quarantineIn,
    @JsonProperty("quarantineOut") Options quarantineOut,
    @JsonProperty("vaccineAcceptance") Boolean vaccineAcceptance) {

    @JsonCreator
    public CountryStatus {
    }

    public static CountryStatus fromDb(CountryStatusRecord countryStatusRecord) {
        return new CountryStatus(
            countryStatusRecord.getCountryName(),
            Status.valueOf(countryStatusRecord.getStatus().toUpperCase()),
            Options.valueOf(countryStatusRecord.getQuarantineIn().toUpperCase()),
            Options.valueOf(countryStatusRecord.getQuarantineOut().toUpperCase()),
            countryStatusRecord.getVaccineAcceptance()
        );
    }
}

enum Status {
    HIGH, MIDDLE, LOW;
}

enum Options {
    YES, NO, MAYBE;
}