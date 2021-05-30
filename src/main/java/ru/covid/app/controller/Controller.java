package ru.covid.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.*;
import ru.covid.app.logic.GenerateQrOperation;
import ru.covid.app.logic.GetSheetOperation;
import ru.covid.app.logic.UploadSheetOperation;
import ru.covid.app.logic.UploadSheetOperation.UploadSheetMessage;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class Controller {

    private final static Logger log = LoggerFactory.getLogger(RestController.class);

    private final ControllerHelper helper;

    private final GetSheetOperation getSheetOperation;
    private final UploadSheetOperation uploadSheetOperation;
    private final GenerateQrOperation generateQrOperation;

    @PostMapping("/sheets")
    @Operation(
            description = "Upload new sheet for certain user",
            summary = "Upload sheet"
    )
    public void uploadSheet(@RequestBody byte[] sheet,
                            @RequestHeader("Authorization") String token) {
        log.info("Controller.uploadSheet.in");
        helper.handleToken(token);
        uploadSheetOperation.process(new UploadSheetMessage(sheet, MDC.get("user")));
        log.info("Controller.uploadSheet.out");
    }

    @PostMapping("/sheets")
    @Operation(
            description = "Get certain sheet by sheetId",
            summary = "Get sheet"
    )
    public byte[] getSheetBySheetId(@RequestParam(value = "sheetId") String sheetId,
                                    @RequestHeader("Authorization") String token) {
        log.info("Controller.getSheetBySheetId.in sheetId = {}", sheetId);
        helper.handleToken(token);
        var sheet = getSheetOperation.process(sheetId);
        log.info("Controller.getSheetBySheetId.out");
        return sheet;
    }

    @GetMapping("/sheets")
    @Operation(
            description = "Generate QR for certain sheet by sheetId",
            summary = "Get QR"
    )
    public byte[] getQrBySheetId(@RequestParam(value = "sheetId") String sheetId,
                                 @RequestHeader("Authorization") String token) {
        log.info("Controller.getQrBySheetId.in sheetId = {}", sheetId);
        helper.handleToken(token);
        var sheet = generateQrOperation.process(sheetId);
        log.info("Controller.getQrBySheetId.out");
        return sheet;
    }
}
