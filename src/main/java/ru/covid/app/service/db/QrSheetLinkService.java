package ru.covid.app.service.db;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.covid.app.jooq.tables.QrSheetLink;
import ru.covid.app.jooq.tables.records.QrSheetLinkRecord;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class QrSheetLinkService {

    private static final Logger log = LoggerFactory.getLogger(QrSheetLinkService.class);

    private final DSLContext db;

    private final QrSheetLink qrSheetLinkTable = QrSheetLink.QR_SHEET_LINK;

    public void insert(QrSheetLinkRecord qrSheetLinkRecord) {
        log.info("QrLinkSheetService.insert.in sheetId = {}, userId = {}", qrSheetLinkRecord.getSheetId(), qrSheetLinkRecord.getUserId());
        db.insertInto(qrSheetLinkTable)
            .set(qrSheetLinkRecord)
            .execute();
        log.info("QrLinkSheetService.insert.out");
    }

    public Optional<QrSheetLinkRecord> findByUserId(String userId) {
        log.info("QrLinkSheetService.findByUserId.in userId = {}", userId);
        var result = db.selectFrom(qrSheetLinkTable)
            .where(qrSheetLinkTable.USER_ID.equal(userId))
            .fetchOptional();
        log.info("QrLinkSheetService.findByUserId.out");
        return result;
    }

    public Optional<QrSheetLinkRecord> findBySheetId(String sheetId) {
        log.info("QrLinkSheetService.findBySheetId.in sheetId = {}", sheetId);
        var result = db.selectFrom(qrSheetLinkTable)
            .where(qrSheetLinkTable.SHEET_ID.equal(sheetId))
            .fetchOptional();
        log.info("QrLinkSheetService.findByUserId.out");
        return result;
    }

    public Optional<QrSheetLinkRecord> findByQrId(String qrId) {
        log.info("QrLinkSheetService.findByQrId.in qrId = {}", qrId);
        var result = db.selectFrom(qrSheetLinkTable)
            .where(qrSheetLinkTable.QR_ID.equal(qrId))
            .fetchOptional();
        log.info("QrLinkSheetService.findByQrId.out");
        return result;
    }

    public void delete(QrSheetLinkRecord record) {
        log.info("QrLinkSheetService.delete.in sheetId = {}, expiredAt = {}", record.getSheetId(), record.getExpiringAt());
        db.deleteFrom(qrSheetLinkTable)
            .where(qrSheetLinkTable.QR_ID.equal(record.getQrId()))
            .execute();
        log.info("QrLinkSheetService.delete.out");
    }
}
