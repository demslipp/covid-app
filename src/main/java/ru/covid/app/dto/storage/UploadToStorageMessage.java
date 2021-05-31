package ru.covid.app.dto.storage;

public record UploadToStorageMessage(byte[] content, String contentType) {
}
