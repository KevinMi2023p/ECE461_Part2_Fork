package com.spring_rest_api.api_paths;

import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring_rest_api.api_paths.entity.Product;
import com.spring_rest_api.api_paths.service.AuthenticateService;
import com.spring_rest_api.api_paths.service.PackageIdService;
import com.spring_rest_api.api_paths.service.PackageService;


import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PathVariable;

// import java.util.Objects;
import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
public class PackageIdController {
    private final ResponseEntity<String> notFoundError = ResponseEntity.status(HttpStatus.NOT_FOUND).body("Package does not exist.");
     private static final Logger logger = LoggerFactory.getLogger(PackageIdController.class);
    @Autowired
    PackageIdService packageIdService;

    @Autowired
    PackageService packageService;

    @Autowired
    AuthenticateService authenticateService;

    @GetMapping("/package/{id}")
    public ResponseEntity<String> packageId(@PathVariable String id ,  @RequestHeader("X-Authorization") String token) throws ExecutionException, InterruptedException {
        if (!validateToken(token)) {
            logger.info("Token sent by User: {}", token);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There is missing field(s) in the PackageID/AuthenticationToken or it is formed improperly, or the AuthenticationToken is invalid.");

        }
        String document_string = packageIdService.getPackage(id);
        return (document_string == null) ? notFoundError : ResponseEntity.status(HttpStatus.OK).body(document_string);
    }

    @PutMapping("/package/{id}")
    public ResponseEntity<String> putMethodName(@PathVariable String id, @RequestBody Product product ,  @RequestHeader("X-Authorization") String token) throws ExecutionException, InterruptedException {
        if (!validateToken(token)) {
            logger.info("Token sent by User: {}", token);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There is missing field(s) in the PackageID/AuthenticationToken or it is formed improperly, or the AuthenticationToken is invalid.");

        }
        String old_document_string = packageIdService.getPackage(id);
        if (old_document_string == null)
            return notFoundError;
        
        boolean res = packageIdService.checkSameMetaData(old_document_string, product);
        if (res == false)
            return notFoundError;

        // Couldn't find Cloud Firestore document to update existing data
        // Solution is to delete existing data then reupload new data
        packageIdService.deletePackage(id);
        packageService.savePackage(product);

        String successMsg = "Version is updated.";
        return ResponseEntity.status(HttpStatus.OK).body(successMsg);
    }

    @DeleteMapping("/package/{id}")
    public ResponseEntity<String> deleteMethodName(@PathVariable String id ,  @RequestHeader("X-Authorization") String token) throws ExecutionException, InterruptedException {
        if (!validateToken(token)) {
            logger.info("Token sent by User: {}", token);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There is missing field(s) in the PackageID/AuthenticationToken or it is formed improperly, or the AuthenticationToken is invalid.");

        }
        boolean deletedDoc = packageIdService.deletePackage(id);
        String delMsg = "Package is deleted.";
        return (deletedDoc == false) ? notFoundError : ResponseEntity.status(HttpStatus.OK).body(delMsg);
    }

    private boolean validateToken(String token) {
        try {
            return authenticateService.validateJwtToken(token);
        } catch (Exception e) {
            return false;
        }
    }

}