package ru.covid.app.dto.storage;

import ru.covid.app.jooq.tables.records.SheetRecord;

public record DownloadFromStorageMessage(String sheetId, String bucket) {

    public static DownloadFromStorageMessage fromRecord(SheetRecord sheetRecord) {
        return new DownloadFromStorageMessage(sheetRecord.getSheetId(), sheetRecord.getS3Bucket());
    }
}
