package ru.covid.app.logic;

public class UploadSheetOperation {

    public void process(UploadSheetMessage message) {

    }

    public static record UploadSheetMessage(byte[] content, String userId) {
    }
}
