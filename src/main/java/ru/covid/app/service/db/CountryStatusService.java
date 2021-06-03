package ru.covid.app.service.db;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.covid.app.jooq.tables.CountryStatus;
import ru.covid.app.jooq.tables.records.CountryStatusRecord;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CountryStatusService {

    private static final Logger log = LoggerFactory.getLogger(CountryStatusService.class);

    private final DSLContext db;

    private final CountryStatus countryStatus = CountryStatus.COUNTRY_STATUS;

    public Optional<CountryStatusRecord> findByCountryName(String countryName) {
        log.info("CountryStatusService.findByCountryName.in countryName = {}", countryName);
        var result = db
            .selectFrom(countryStatus)
            .where(countryStatus.COUNTRY_NAME.equal(countryName))
            .fetchOptional();
        log.info("CountryStatusService.findByCountryName.out");
        return result;
    }
}
