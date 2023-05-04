package com.spring_rest_api.api_paths;

import org.springframework.web.bind.annotation.RestController;

import com.spring_rest_api.api_paths.service.AuthenticateService;
import com.spring_rest_api.api_paths.service.PackageNameService;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;


@RestController
public class PackageByNameController {
    private final ResponseEntity<String> notFoundError = ResponseEntity.status(HttpStatus.NOT_FOUND).body("Package does not exist.");

    @Autowired
    PackageNameService packageNameService;

    @Autowired
    AuthenticateService authenticateService;
    
	@GetMapping(value="/package/byName/{name}", produces = "application/json")
	public ResponseEntity<String> packageByName(@PathVariable String name , @RequestHeader("X-Authorization") String token) throws ExecutionException, InterruptedException {
        if (!authenticateService.validateAuthHeaderForUser(token)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    "There is missing field(s) in the PackageID/AuthenticationToken or it is formed improperly, or the AuthenticationToken is invalid.");

        }
        String result = packageNameService.getByName(name);
        
        return (result == null) ? notFoundError : ResponseEntity.ok().body(result);
	}

    @DeleteMapping(value = "/package/byName/{name}")
    public ResponseEntity<String> deleteMethodName(@PathVariable String name , @RequestHeader("X-Authorization") String token) throws ExecutionException, InterruptedException {
        if (!authenticateService.validateAuthHeaderForAdmin(token)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    "There is missing field(s) in the PackageID/AuthenticationToken or it is formed improperly, or the AuthenticationToken is invalid.");

        }
        Integer res = packageNameService.removeByName(name);
        if (res == 0)
            return notFoundError;
        
        String successMsg = "Package is deleted.";
        return ResponseEntity.ok().body(successMsg);
    }

}
