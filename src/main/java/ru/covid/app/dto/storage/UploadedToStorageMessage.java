package ru.covid.app.dto.storage;

public record UploadedToStorageMessage(String sheetId, String bucket) {
}