package com.spring_rest_api.api_paths;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.spring_rest_api.api_paths.entity.RegexSchema;
import com.spring_rest_api.api_paths.service.AuthenticateService;
import com.spring_rest_api.api_paths.service.RegexService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
@DependsOn("firestoreInitialization")
public class PackageByRegexController {
    private final ResponseEntity<String> notFoundError = ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body("No package found under this regex.");

    @Autowired
    AuthenticateService authenticateService;

    @Autowired
    RegexService regexService;

    @PostMapping(value = "/package/byRegEx", produces = "application/json")
    public ResponseEntity<String> postMethodName(@RequestBody RegexSchema regexSchema,
            @RequestHeader("X-Authorization") String token) throws ExecutionException, InterruptedException {
        if (!authenticateService.validateAuthHeaderForUser(token)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    "There is missing field(s) in the PackageID/AuthenticationToken or it is formed improperly, or the AuthenticationToken is invalid.");

        }
        String result = regexService.getVersionName(regexSchema);
        if (result == null)
            return notFoundError;

        return ResponseEntity.ok().body(result);
    }

}