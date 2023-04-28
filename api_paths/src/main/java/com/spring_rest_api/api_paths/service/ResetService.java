package com.spring_rest_api.api_paths.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ResetService {
    private static final String USER_COLLECTION_NAME = "users";
    private static final String PACKAGE_COLLECTION_NAME = "packages";
    private static final Logger logger = LoggerFactory.getLogger(ResetService.class);

    @Autowired
    AuthenticateService authenticateService;

    public void resetAll() {
        deleteAllUsers();
        deleteAllPackages();
        authenticateService.createDefaultUser();
    }

    private void deleteAllUsers() {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        try {
            dbFirestore.collection(USER_COLLECTION_NAME).listDocuments().forEach(docRef -> {
                try {
                    ApiFuture<DocumentSnapshot> document = docRef.get();
                    DocumentSnapshot doc = document.get();
                    if (doc.exists()) {
                        docRef.delete();
                    }
                } catch (Exception e) {
                    logger.error("Error deleting user: {}", e.getMessage());
                }
            });
            logger.info("All users deleted.");
        } catch (Exception e) {
            logger.error("Error deleting all users: {}", e.getMessage());
        }
    }

    private void deleteAllPackages() {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        try {
            dbFirestore.collection(PACKAGE_COLLECTION_NAME).listDocuments().forEach(docRef -> {
                try {
                    ApiFuture<DocumentSnapshot> document = docRef.get();
                    DocumentSnapshot doc = document.get();
                    if (doc.exists()) {
                        docRef.delete();
                    }
                } catch (Exception e) {
                    logger.error("Error deleting package: {}", e.getMessage());
                }
            });
            logger.info("All packages deleted.");
        } catch (Exception e) {
            logger.error("Error deleting all packages: {}", e.getMessage());
        }
    }
}
