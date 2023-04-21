package com.spring_rest_api.api_paths;

import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring_rest_api.api_paths.entity.Product;
import com.spring_rest_api.api_paths.service.PackageIdService;
import com.spring_rest_api.api_paths.service.PackageService;


import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Objects;
import java.util.concurrent.ExecutionException;


@RestController
public class PackageIdController {
    private ResponseEntity<String> notFoundError = ResponseEntity.status(HttpStatus.NOT_FOUND).body("Package does not exist.");

    @Autowired
    PackageIdService packageIdService;

    @Autowired
    PackageService packageService;

    @GetMapping("/package/{id}")
    public ResponseEntity<String> packageId(@PathVariable String id) throws ExecutionException, InterruptedException {
        String document_string = packageIdService.getPackage(id);
        return (document_string == null) ? notFoundError : ResponseEntity.status(HttpStatus.OK).body(document_string);
    }

    @PutMapping("/package/{id}")
    public ResponseEntity<String> putMethodName(@PathVariable String id, @RequestBody Product product) throws ExecutionException, InterruptedException {
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
    public ResponseEntity<String> deleteMethodName(@PathVariable String id) throws ExecutionException, InterruptedException {
        boolean deletedDoc = packageIdService.deletePackage(id);
        String delMsg = "Package is deleted.";
        return (deletedDoc == false) ? notFoundError : ResponseEntity.status(HttpStatus.OK).body(delMsg);
    }

}