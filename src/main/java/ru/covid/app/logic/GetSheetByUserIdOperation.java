package ru.covid.app.logic;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.covid.app.dto.logic.SheetWithContent;
import ru.covid.app.dto.storage.DownloadFromStorageMessage;
import ru.covid.app.exception.HttpCodeException;
import ru.covid.app.service.db.SheetService;
import ru.covid.app.service.storage.StorageService;

import static ru.covid.app.exception.ApplicationError.INTERNAL_ERROR;
import static ru.covid.app.exception.ApplicationError.SHEET_NOT_FOUND_BY_USER_ID;

@Component
@RequiredArgsConstructor
public class GetSheetByUserIdOperation {

    private static final Logger log = LoggerFactory.getLogger(GetSheetByUserIdOperation.class);

    private final SheetService sheetService;
    private final StorageService storageService;

    public SheetWithContent process(String userId) {
        log.info("GetSheetByUserIdOperation.process.in userId = {}", userId);
        try {
            var result = internalProcess(userId);
            log.info("GetSheetByUserIdOperation.process.out");
            return result;
        } catch (HttpCodeException e) {
            log.error("GetSheetByUserIdOperation.process.thrown", e);
            throw e;
        } catch (Exception e) {
            log.error("GetSheetByUserIdOperation.process.thrown", e);
            throw INTERNAL_ERROR.exception(e);
        }
    }

    private SheetWithContent internalProcess(String userId) {
        return sheetService
            .findByUserId(userId)
            .map(DownloadFromStorageMessage::fromRecord)
            .map(storageService::download)
            .orElseThrow(SHEET_NOT_FOUND_BY_USER_ID::exception);
    }
}
