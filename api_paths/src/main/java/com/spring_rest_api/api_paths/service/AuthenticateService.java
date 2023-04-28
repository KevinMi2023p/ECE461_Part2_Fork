package com.spring_rest_api.api_paths.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.spring_rest_api.api_paths.entity.User;
import com.spring_rest_api.api_paths.entity.UserAuthenticationInfo;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

@Service
public class AuthenticateService {
    private static final String COLLECTION_NAME = "Users";
    private static final String JWT_SECRET = "GIyoqsMwGPv2YEStDNat1qaXXbOH8lmwkvbUODyzoF8="; // Replace with your own secret
    private static final long EXPIRATION_TIME = 36000000; // 1 day in milliseconds
    private static final String TOKEN_USAGE_COLLECTION_NAME = "TokenUsage";
    private static final long MAX_TOKEN_USAGE = 1000;

    private static final Logger logger = LoggerFactory.getLogger(AuthenticateService.class);

    public User saveUser(User user, UserAuthenticationInfo authInfo) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference docRef = dbFirestore.collection(COLLECTION_NAME).document(user.getName());
        ApiFuture<DocumentSnapshot> document = docRef.get();
        DocumentSnapshot doc = document.get();
    
        logger.info("Checking if user {} exists in Firestore", user.getName());
        
        if (doc.exists()) {
            logger.warn("User already exists: {}", user.getName());
            logger.warn("Document data for user {}: {}", user.getName(), doc.getData());
            return null; // User already exists
        } else {
            // Set the authentication info to the user object
            user.setUserAuthenticationInfo(new HashMap<String, String>() {{
                put("password", authInfo.getPassword());
            }});
            logger.info("Saving UserAuthenticationInfo: {}", authInfo); // Add this log statement
    
            ApiFuture<WriteResult> collectionApiFuture = dbFirestore.collection(COLLECTION_NAME).document(user.getName()).set(user);
            logger.info("User saved: {}", user.getName());
            return user; // Successfully saved user
        }
    }
    
    
    

    public void removeUser(String username) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference docRef = dbFirestore.collection(COLLECTION_NAME).document(username);
        ApiFuture<DocumentSnapshot> document = docRef.get();
        DocumentSnapshot doc = document.get();

        if (doc.exists()) {
            ApiFuture<WriteResult> writeResult = docRef.delete();
        } else {
            throw new IllegalArgumentException("User not found.");
        }
    }


    public String authenticateUser(User user, UserAuthenticationInfo authInfo) throws ExecutionException, InterruptedException, UnsupportedEncodingException {
        Firestore dbfirestore = FirestoreClient.getFirestore();
        DocumentReference docRef = dbfirestore.collection(COLLECTION_NAME).document(user.getName());
        ApiFuture<DocumentSnapshot> document = docRef.get();
        DocumentSnapshot doc = document.get();
    
        if (doc.exists()) {
            String storedPassword = doc.get("userAuthenticationInfo.password", String.class);
            Map<String, String> storedAuthInfo = doc.toObject(User.class).getUserAuthenticationInfo();
            storedPassword = storedAuthInfo.get("password");
            logger.info("Stored UserAuthenticationInfo: {}", storedAuthInfo);
            logger.info("Stored password: {}", storedPassword);
            logger.info("Provided password: {}", authInfo.getPassword());
    
            if (authInfo.getPassword().equals(storedPassword)) {
                String token = generateJwtToken(user.getName());
                storeTokenUsage(token, user.getName()); // Pass the username as a parameter
                return token;
            } else {
                throw new IllegalArgumentException("Invalid password.");
            }
        } else {
            throw new IllegalArgumentException("User not found.");
        }
    }

    private String generateJwtToken(String username) throws UnsupportedEncodingException {
        Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET);
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + EXPIRATION_TIME);

        return JWT.create()
                .withIssuer("auth0")
                .withSubject(username)
                .withIssuedAt(now)
                .withExpiresAt(expirationDate)
                .sign(algorithm);
    }

    private void storeTokenUsage(String token, String username) throws ExecutionException, InterruptedException {
        String hashedToken = token;
        Firestore dbFirestore = FirestoreClient.getFirestore();
        CollectionReference tokenUsageCollection = dbFirestore.collection(TOKEN_USAGE_COLLECTION_NAME);
    
        // Find the existing token entry for the user
        Query query = tokenUsageCollection.whereEqualTo("username", username);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
    
        if (!documents.isEmpty()) {
            // Update the existing token entry
            QueryDocumentSnapshot existingDoc = documents.get(0);
            String existingDocId = existingDoc.getId();
            DocumentReference docRef = tokenUsageCollection.document(existingDocId);
            ApiFuture<WriteResult> collectionApiFuture = docRef.update("hashedToken", hashedToken, "usageCount", 1L, "expirationTime", new Date(System.currentTimeMillis() + EXPIRATION_TIME));
        } else {
            // Create a new token entry
            Map<String, Object> data = new HashMap<>();
            data.put("hashedToken", hashedToken);
            data.put("username", username);
            data.put("usageCount", 1L);
            data.put("expirationTime", new Date(System.currentTimeMillis() + EXPIRATION_TIME));
            ApiFuture<DocumentReference> collectionApiFuture = tokenUsageCollection.add(data);
        }
    }
    


    public boolean validateJwtToken(String token) throws JWTVerificationException, UnsupportedEncodingException {
        try {
            Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET);
            JWTVerifier verifier = JWT.require(algorithm).withIssuer("auth0").build();
            DecodedJWT jwt = verifier.verify(token);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    // The following is for default username and secret
    @PostConstruct
    public void createDefaultUser() {
        String defaultUsername = "admin";
        String defaultPassword = "admin";
        boolean defaultIsAdmin = true;

        try {
            // Check if the default user already exists
            Firestore dbFirestore = FirestoreClient.getFirestore();
            DocumentReference docRef = dbFirestore.collection(COLLECTION_NAME).document(defaultUsername);
            ApiFuture<DocumentSnapshot> document = docRef.get();
            DocumentSnapshot doc = document.get();

            if (!doc.exists()) {
                // Create a new user with the specified username and password
                User defaultUser = new User();
                defaultUser.setName(defaultUsername);

                Secret userSecret = new Secret();
                userSecret.setPassword(defaultPassword);

                defaultUser.setIsAdmin(defaultIsAdmin);

                saveUser(defaultUser, userSecret);
                logger.info("Default user created: {}", defaultUsername);
            } else {
                logger.info("Default user already exists: {}", defaultUsername);
            }
        } catch (ExecutionException | InterruptedException e) {
            logger.error("Error creating default user: {}", e.getMessage());
        }
    }

}
