package com.spring_rest_api.api_paths.FirebaseInitialization;

import jakarta.annotation.PostConstruct;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.FirebaseApp;
import com.google.auth.oauth2.GoogleCredentials;

@Configuration
public class FirestoreInitialization {

    @PostConstruct
    public void initialization() {
        FileInputStream serviceAccount = null;
        try {
            ClassPathResource resource = new ClassPathResource("firestore_credentials.json");
            serviceAccount = new FileInputStream(resource.getFile());

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
