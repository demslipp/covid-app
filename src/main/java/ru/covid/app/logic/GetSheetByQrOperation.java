package ru.covid.app.logic;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.covid.app.dto.logic.SheetWithContent;
import ru.covid.app.dto.storage.DownloadFromStorageMessage;
import ru.covid.app.exception.HttpCodeException;
import ru.covid.app.jooq.tables.records.QrSheetLinkRecord;
import ru.covid.app.service.db.QrSheetLinkService;
import ru.covid.app.service.db.SheetService;
import ru.covid.app.service.storage.StorageService;

import java.time.LocalDateTime;

import static ru.covid.app.exception.ApplicationError.INTERNAL_ERROR;
import static ru.covid.app.exception.ApplicationError.QR_LINK_IS_EXPIRED;
import static ru.covid.app.exception.ApplicationError.SHEET_NOT_FOUND_BY_QR;

@Component
@RequiredArgsConstructor
public class GetSheetByQrOperation {

    private static final Logger log = LoggerFactory.getLogger(GetSheetByQrOperation.class);

    private final QrSheetLinkService qrSheetLinkService;
    private final SheetService sheetService;
    private final StorageService storageService;

    public SheetWithContent process(String qrId) {
        log.info("GetSheetByQrOperation.process.in qrId = {}", qrId);
        try {
            var result = internalProcess(qrId);
            log.info("GetSheetByQrOperation.process.out");
            return result;
        } catch (HttpCodeException e) {
            log.error("GetSheetByQrOperation.process.thrown", e);
            throw e;
        } catch (Exception e) {
            log.error("GetSheetByQrOperation.process.thrown", e);
            throw INTERNAL_ERROR.exception(e);
        }
    }

    private SheetWithContent internalProcess(String qrId) {
        return qrSheetLinkService
            .findByQrId(qrId)
            .map(r -> {
                if (r.getExpiringAt().isBefore(LocalDateTime.now())) {
                    qrSheetLinkService.delete(r);
                    throw QR_LINK_IS_EXPIRED.exception();
                }
                return r;
            })
            .map(QrSheetLinkRecord::getSheetId)
            .flatMap(sheetService::findBySheetId)
            .map(DownloadFromStorageMessage::fromRecord)
            .map(storageService::download)
            .orElseThrow(SHEET_NOT_FOUND_BY_QR::exception);
    }
}
