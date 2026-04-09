//package com.betting_app.dashboard.config;
//
//import com.google.auth.oauth2.GoogleCredentials;
//import com.google.firebase.FirebaseApp;
//import com.google.firebase.FirebaseOptions;
//import jakarta.annotation.PostConstruct;
//import org.springframework.context.annotation.Configuration;
//
//import java.io.InputStream;
//
//@Configuration
//public class FirebaseConfig {
//
//    @PostConstruct
//    public void init() {
//        try {
//            if (!FirebaseApp.getApps().isEmpty()) {
//                return;
//            }
//
//            InputStream serviceAccount = getClass()
//                    .getClassLoader()
//                    .getResourceAsStream("firebase-service-account.json");
//
//            if (serviceAccount == null) {
//                throw new RuntimeException("firebase-service-account.json not found");
//            }
//
//            FirebaseOptions options = FirebaseOptions.builder()
//                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
//                    .setDatabaseUrl("https://betting-app-dashboard2-default-rtdb.firebaseio.com/")
//                    .build();
//
//            FirebaseApp.initializeApp(options);
//
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to initialize Firebase", e);
//        }
//    }
//}



package com.betting_app.dashboard.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void init() {
        try {
            if (!FirebaseApp.getApps().isEmpty()) {
                return;
            }

            InputStream serviceAccount = getClass()
                    .getClassLoader()
                    .getResourceAsStream("firebase-service-account.json");

            if (serviceAccount == null) {
                throw new RuntimeException("firebase-service-account.json not found");
            }

            byte[] bytes = serviceAccount.readAllBytes();

            if (bytes.length == 0) {
                throw new RuntimeException("firebase-service-account.json is empty");
            }

            String content = new String(bytes, StandardCharsets.UTF_8).trim();

            System.out.println("FIRST 120 CHARS OF FIREBASE FILE:");
            System.out.println(content.substring(0, Math.min(content.length(), 120)));

            if (!content.startsWith("{")) {
                throw new RuntimeException("firebase-service-account.json does not start with '{'");
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(
                            GoogleCredentials.fromStream(new ByteArrayInputStream(bytes))
                    )
                    .setDatabaseUrl("https://betting-app-dashboard2-default-rtdb.firebaseio.com/")
                    .build();

            FirebaseApp.initializeApp(options);
            System.out.println("Firebase initialized successfully");

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize Firebase", e);
        }
    }
}