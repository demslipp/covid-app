package ru.covid.app.dto.logic;

import org.springframework.http.ResponseEntity;

public record SheetWithContent (byte[] content, String contentType) {

    public ResponseEntity<byte[]> asResponse() {
        return ResponseEntity
            .ok()
            .header("Content-Type", contentType())
            .body(content());
    }
}
