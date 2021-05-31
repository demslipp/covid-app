package ru.covid.app.service.storage;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import ru.covid.app.dto.logic.SheetWithContent;
import ru.covid.app.dto.storage.DownloadFromStorageMessage;
import ru.covid.app.dto.storage.UploadToStorageMessage;
import ru.covid.app.dto.storage.UploadedToStorageMessage;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class StorageService {

    private Storage storage;

    private final String BUCKET_NAME = "covid-app-rus.appspot.com";

    @EventListener(ApplicationStartedEvent.class)
    public void onStartUp() {
        try {
            var serviceAccount = new ClassPathResource("service_account_key.json");
            this.storage = StorageOptions.newBuilder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount.getInputStream()))
                .build()
                .getService();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public UploadedToStorageMessage upload(UploadToStorageMessage message) {
        var sheetId = UUID.randomUUID().toString();
        var blobId = BlobId.of(BUCKET_NAME, sheetId);
        var blobInfo = BlobInfo.newBuilder(blobId)
            .setContentType(message.contentType())
            .build();
        storage.create(blobInfo, message.content());
        return new UploadedToStorageMessage(sheetId, BUCKET_NAME);
    }

    public SheetWithContent download(DownloadFromStorageMessage message) {
        var blobId = BlobId.of(message.bucket(), message.sheetId());
        var blob = storage.get(blobId);
        return new SheetWithContent(blob.getContent(), blob.getContentType());
    }
}
