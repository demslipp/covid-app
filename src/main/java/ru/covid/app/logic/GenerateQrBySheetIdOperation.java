package ru.covid.app.logic;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.covid.app.dto.GenerateQrRequest;
import ru.covid.app.exception.HttpCodeException;
import ru.covid.app.jooq.tables.records.QrSheetLinkRecord;
import ru.covid.app.jooq.tables.records.SheetRecord;
import ru.covid.app.service.db.QrSheetLinkService;
import ru.covid.app.service.db.SheetService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAmount;
import java.util.UUID;
import javax.imageio.ImageIO;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static ru.covid.app.exception.ApplicationError.CREATING_QR_ERROR;
import static ru.covid.app.exception.ApplicationError.INTERNAL_ERROR;
import static ru.covid.app.exception.ApplicationError.SHEET_NOT_FOUND_BY_SHEET_ID;

@Component
@RequiredArgsConstructor
public class GenerateQrBySheetIdOperation {

    private static final Logger log = LoggerFactory.getLogger(GenerateQrBySheetIdOperation.class);
    private static final String URL_PATTERN = "http://localhost/api/qr?qrId=%s";
    private static final TemporalAmount TTL_DELTA = Duration.ofMinutes(60);

    private final QrSheetLinkService qrSheetLinkService;
    private final SheetService sheetService;

    private final Writer qrWriter = new QRCodeWriter();

    public byte[] process(GenerateQrRequest sheetId) {
        log.info("GenerateQrBySheetIdOperation.process.in sheetId = {}", sheetId);
        try {
            var result = internalProcess(sheetId);
            log.info("GenerateQrBySheetIdOperation.process.out");
            return result;
        } catch (HttpCodeException e) {
            log.error("GenerateQrBySheetIdOperation.process.thrown", e);
            throw e;
        } catch (Exception e) {
            log.error("GenerateQrBySheetIdOperation.process.thrown", e);
            throw INTERNAL_ERROR.exception(e);
        }
    }

    private byte[] internalProcess(GenerateQrRequest request) {
        return ofNullable(request.userId())
            .map(sheetService::findByUserId)
            .orElse(sheetService.findBySheetId(request.sheetId()))
            .map(this::generateQrAndSaveUrl)
            .orElseThrow(SHEET_NOT_FOUND_BY_SHEET_ID::exception);
    }

    private byte[] generateQrAndSaveUrl(SheetRecord sheetRecord) {
        var qrId = UUID.randomUUID().toString();
        var url = format(URL_PATTERN, qrId);
        qrSheetLinkService.insert(qrSheetLinkRecord(sheetRecord, qrId));
        return generateQr(url);
    }

    private byte[] generateQr(String url) {
        BitMatrix bitMatrix;
        try (var bos = new ByteArrayOutputStream()) {
            bitMatrix = qrWriter.encode(url, BarcodeFormat.QR_CODE, 200, 200);
            var qr = MatrixToImageWriter.toBufferedImage(bitMatrix);
            ImageIO.write(qr, "png", bos);
            return bos.toByteArray();
        } catch (WriterException | IOException e) {
            throw CREATING_QR_ERROR.exception(e);
        }
    }

    private QrSheetLinkRecord qrSheetLinkRecord(SheetRecord sheetRecord, String qrId) {
        var qrSheetLinkRecord = new QrSheetLinkRecord();
        qrSheetLinkRecord.setSheetId(sheetRecord.getSheetId());
        qrSheetLinkRecord.setExpiringAt(LocalDateTime.now().plus(TTL_DELTA));
        qrSheetLinkRecord.setUserId(sheetRecord.getUserId());
        qrSheetLinkRecord.setQrId(qrId);
        return qrSheetLinkRecord;
    }
}
