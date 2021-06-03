package ru.covid.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.covid.app.dto.GenerateQrRequest;
import ru.covid.app.dto.logic.CountryStatus;
import ru.covid.app.dto.logic.UploadSheetMessage;
import ru.covid.app.logic.GenerateQrBySheetIdOperation;
import ru.covid.app.logic.GetSheetByQrOperation;
import ru.covid.app.logic.GetSheetByUserIdOperation;
import ru.covid.app.logic.GetSheetIdByUserIdOperation;
import ru.covid.app.logic.UploadSheetOperation;
import ru.covid.app.service.db.CountryStatusService;

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
    private final GetSheetIdByUserIdOperation getSheetIdByUserIdOperation;

    private final CountryStatusService countryStatusService;

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

    @GetMapping("/qr/generate")
    @Operation(
            description = "Generate QR for certain sheet by sheetId",
            summary = "Get QR"
    )
    public ResponseEntity<byte[]> generateQr(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        helper.handleToken(token);
        var userId = MDC.get("user");
        var sheetId = getSheetIdByUserIdOperation.process(userId);
        var sheet = generateQrBySheetIdOperation.process(new GenerateQrRequest(sheetId, userId));
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

    @GetMapping("/country/{country}/status")
    @Operation(
        description = "Get covid status of a certain country by its name",
        summary = "Get country covid status"
    )
    public ResponseEntity<CountryStatus> statusByCountryName(@PathVariable(value = "country") String country) {
        log.info("Controller.statusByCountryName.in country = {}", country);
        var status = countryStatusService.findByCountryName(country)
            .map(CountryStatus::fromDb)
            .map(r ->
                ResponseEntity
                    .ok()
                    .header("Content-Type", "application/json")
                    .body(r)
            )
            .orElse(ResponseEntity.notFound().build());
        log.info("Controller.statusByCountryName.out");
        return status;
    }
}
