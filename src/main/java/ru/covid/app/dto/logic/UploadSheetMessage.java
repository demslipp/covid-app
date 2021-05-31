package ru.covid.app.dto.logic;

import ru.covid.app.dto.storage.UploadToStorageMessage;

public record UploadSheetMessage(byte[] content, String contentType, String userId) {

    public UploadToStorageMessage toStorage() {
        return new UploadToStorageMessage(content, contentType);
    }
}
