package ru.covid.app;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Bucket;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;
import org.springframework.boot.SpringApplication;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class Application {

    private static final InputStream KEY = Thread.currentThread().getContextClassLoader().getResourceAsStream("service_account_key.json");

    public static void main(String[] args) throws IOException {
        var options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(Objects.requireNonNull(KEY)))
                .build();
        FirebaseApp.initializeApp(options);
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(credential)
                .setDatabaseUrl(projectUrl)
                .setStorageBucket("YOUR BUCKET LINK")
                .build();

        FirebaseApp fireApp = FirebaseApp.initializeApp(options);

        StorageClient storageClient = StorageClient.getInstance(fireApp);
        InputStream testFile = new FileInputStream("YOUR FILE PATH");
        String blobString = "NEW_FOLDER/" + "FILE_NAME.EXT";

        storageClient.bucket().create(blobString, testFile , Bucket.BlobWriteOption.userProject("YOUR PROJECT ID"));

        SpringApplication.run(Application.class);
    }
}
