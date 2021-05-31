package ru.covid.app.service.db;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.covid.app.jooq.tables.Sheet;
import ru.covid.app.jooq.tables.records.SheetRecord;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SheetService {

    private static final Logger log = LoggerFactory.getLogger(SheetService.class);

    private final DSLContext db;

    private final Sheet sheetTable = Sheet.SHEET;

    public Optional<SheetRecord> findByUserId(String userId) {
        log.info("SheetService.findByUserId.in userId = {}", userId);
        var result = db.selectFrom(sheetTable)
            .where(sheetTable.USER_ID.equal(userId))
            .fetchOptional();
        log.info("SheetService.findByUserId.out");
        return result;
    }

    public Optional<SheetRecord> findBySheetId(String sheetId) {
        log.info("SheetService.findBySheetId.in sheetId = {}", sheetId);
        var result = db.selectFrom(sheetTable)
            .where(sheetTable.SHEET_ID.equal(sheetId))
            .fetchOptional();
        log.info("SheetService.findBySheetId.out");
        return result;
    }

    public void insertOnConflictUpdate(SheetRecord sheet) {
        log.info("SheetService.insertOnConflictUpdate.in sheetId = {}", sheet.getSheetId());

        db.insertInto(sheetTable)
            .set(sheet)
            .onConflictOnConstraint(sheetTable.getPrimaryKey())
            .doUpdate()
            .set(sheetTable.SHEET_ID, sheet.getSheetId())
            .where(sheetTable.USER_ID.equal(sheet.getUserId()))
            .execute();
        log.info("SheetService.insertOnConflictUpdate.out");
    }

    public void deleteByUserId(String userId) {
        log.info("SheetService.deleteSheetByUserId.in userId = {}", userId);
        db.deleteFrom(sheetTable)
            .where(sheetTable.USER_ID.equal(userId))
            .execute();
        log.info("SheetService.deleteSheetByUserId.out");
    }
}
