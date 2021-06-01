package ru.covid.app.logic;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.covid.app.exception.HttpCodeException;
import ru.covid.app.jooq.tables.records.SheetRecord;
import ru.covid.app.service.db.SheetService;

import static ru.covid.app.exception.ApplicationError.INTERNAL_ERROR;
import static ru.covid.app.exception.ApplicationError.SHEET_NOT_FOUND_BY_USER_ID;

@Component
@RequiredArgsConstructor
public class GetSheetIdByUserIdOperation {
    private static final Logger log = LoggerFactory.getLogger(GetSheetIdByUserIdOperation.class);

    private final SheetService sheetService;

    public String process(String userId) {
        log.info("GetSheetIdByUserIdOperation.process.in userId = {}", userId);
        try {
            var result = internalProcess(userId);
            log.info("GetSheetIdByUserIdOperation.process.out");
            return result;
        } catch (HttpCodeException e) {
            log.error("GetSheetIdByUserIdOperation.process.thrown", e);
            throw e;
        } catch (Exception e) {
            log.error("GetSheetIdByUserIdOperation.process.thrown", e);
            throw INTERNAL_ERROR.exception(e);
        }
    }

    private String internalProcess(String userId) {
        return sheetService
                .findByUserId(userId)
                .map(SheetRecord::getSheetId)
                .orElseThrow(SHEET_NOT_FOUND_BY_USER_ID::exception);
    }
}
