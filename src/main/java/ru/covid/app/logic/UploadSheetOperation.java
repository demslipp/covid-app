package ru.covid.app.logic;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.covid.app.dto.logic.UploadSheetMessage;
import ru.covid.app.dto.storage.UploadedToStorageMessage;
import ru.covid.app.exception.HttpCodeException;
import ru.covid.app.jooq.tables.records.SheetRecord;
import ru.covid.app.service.db.QrSheetLinkService;
import ru.covid.app.service.db.SheetService;
import ru.covid.app.service.storage.StorageService;

import static ru.covid.app.exception.ApplicationError.INTERNAL_ERROR;

@Component
@RequiredArgsConstructor
public class UploadSheetOperation {

    private static final Logger log = LoggerFactory.getLogger(QrSheetLinkService.class);

    private final SheetService sheetService;
    private final StorageService storageService;

    public void process(UploadSheetMessage message) {
        log.info("UploadSheetOperation.process.in contentType = {}, userId = {}", message.contentType(), message.userId());
        try {
            internalProcess(message);
        } catch (HttpCodeException e) {
            log.error("UploadSheetOperation.process.thrown", e);
            throw e;
        } catch (Exception e) {
            log.error("UploadSheetOperation.process.thrown", e);
            throw INTERNAL_ERROR.exception(e);
        }
        log.info("UploadSheetOperation.process.out");
    }

    private void internalProcess(UploadSheetMessage message) {
        var uploadedMessage = storageService.upload(message.toStorage());
        sheetService.insertOnConflictUpdate(constructSheetRecord(message, uploadedMessage));
    }

    private SheetRecord constructSheetRecord(UploadSheetMessage uploadSheetMessage, UploadedToStorageMessage uploadedToStorageMessage) {
        var sheetRecord = new SheetRecord();
        sheetRecord.setSheetId(uploadedToStorageMessage.sheetId());
        sheetRecord.setUserId(uploadSheetMessage.userId());
        sheetRecord.setS3Bucket(uploadedToStorageMessage.bucket());
        sheetRecord.setSheetId(uploadedToStorageMessage.sheetId());
        sheetRecord.setContentType(uploadSheetMessage.contentType());
        return sheetRecord;
    }
}
