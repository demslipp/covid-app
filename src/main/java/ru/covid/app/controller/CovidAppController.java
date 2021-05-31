package ru.covid.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.covid.app.dto.GenerateQrRequest;
import ru.covid.app.dto.logic.UploadSheetMessage;
import ru.covid.app.logic.GenerateQrBySheetIdOperation;
import ru.covid.app.logic.GetSheetByQrOperation;
import ru.covid.app.logic.GetSheetByUserIdOperation;
import ru.covid.app.logic.UploadSheetOperation;

import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static ru.covid.app.exception.ValidationError.SHEET_ID_OR_USER_ID_IS_REQUIRED;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CovidAppController {

    private final static Logger log = LoggerFactory.getLogger(RestController.class);

    private final ControllerHelper helper;

    private final UploadSheetOperation uploadSheetOperation;
    private final GetSheetByUserIdOperation getSheetByUserIdOperation;
    private final GenerateQrBySheetIdOperation generateQrBySheetIdOperation;
    private final GetSheetByQrOperation getSheetByQrOperation;

    @PostMapping("/sheets")
    @Operation(
        description = "Upload new sheet for certain user",
        summary = "Upload sheet"
    )
    public void uploadSheet(@RequestBody byte[] sheet,
                            @RequestHeader("Authorization") String token,
                            @RequestHeader("Content-Type") String contentType) {
        log.info("Controller.uploadSheet.in");
        helper.handleToken(token);
        uploadSheetOperation.process(new UploadSheetMessage(sheet, contentType, MDC.get("user")));
        log.info("Controller.uploadSheet.out");
    }

    @GetMapping("/sheets")
    @Operation(
        description = "Get certain sheet by sheetId",
        summary = "Get sheet"
    )
    public ResponseEntity<byte[]> getSheetByUserId(@RequestHeader("Authorization") String token) {
        helper.handleToken(token);
        var userId = MDC.get("user");
        log.info("Controller.getSheetBySheetId.in userId = {}", userId);
        var sheet = getSheetByUserIdOperation.process(userId);
        log.info("Controller.getSheetBySheetId.out");
        return sheet.asResponse();
    }

    @PostMapping("/qr")
    @Operation(
        description = "Generate QR for certain sheet by sheetId",
        summary = "Get QR"
    )
    public ResponseEntity<byte[]> generateQr(@RequestParam(value = "sheetId", required = false) String sheetId,
                                             @RequestBody(required = false) GenerateQrRequest generateQrRequest,
                                             @RequestHeader("Authorization") String token) {
        log.info("Controller.getQrBySheetId.in sheetId = {}", sheetId);
        helper.handleToken(token);
        if (isNull(generateQrRequest)) {
            generateQrRequest = new GenerateQrRequest(
                ofNullable(sheetId)
                    .orElseThrow(SHEET_ID_OR_USER_ID_IS_REQUIRED::exception),
                MDC.get("user"));
        }
        var sheet = generateQrBySheetIdOperation.process(generateQrRequest);
        log.info("Controller.getQrBySheetId.out");
        return ResponseEntity
            .ok()
            .header("Content-Type", "image/png")
            .body(sheet);
    }

    @GetMapping("/qr")
    @Operation(
        description = "Get certain sheet by qrId",
        summary = "Get sheet"
    )
    public ResponseEntity<byte[]> getSheetByQr(@RequestParam(value = "qrId") String qrId) {
        log.info("Controller.getSheetByQr.in qrId = {}", qrId);
        var sheet = getSheetByQrOperation.process(qrId);
        log.info("Controller.getSheetByQr.out");
        return sheet.asResponse();
    }
}
