package ru.covid.app.service.storage;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class StorageService {

    private Storage storage;

    private final String BUCKET_NAME = "";

    @EventListener
    public void init(ApplicationReadyEvent event) {
        try {
            var serviceAccount = new ClassPathResource("firebase.json");
            storage = StorageOptions.newBuilder().
                    setCredentials(GoogleCredentials.fromStream(serviceAccount.getInputStream())).
                    setProjectId("YOUR_PROJECT_ID").build().getService();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public UploadedMessage upload(UploadMessage message) {
        var sheetId = UUID.randomUUID().toString();
        var map = new HashMap<String, String>();
        map.put("firebaseStorageDownloadTokens", sheetId);
        var bucket = "";
        var blobId = BlobId.of(bucket, sheetId);
        var blobInfo = BlobInfo.newBuilder(blobId)
                .setMetadata(map)
                .setContentType(message.contentType())
                .build();
        storage.create(blobInfo, message.content());
        return new UploadedMessage(sheetId, bucket);
    }

    public byte[] download(DownloadMessage message) {
        var blobId = BlobId.of(message.bucket(), message.sheetId);
        return storage.get(blobId).getContent();
    }

    public static record UploadMessage(byte[] content, String contentType) {
    }

    public static record UploadedMessage(String sheetId, String bucket) {
    }

    public static record DownloadMessage(String sheetId, String bucket) {
    }
}
