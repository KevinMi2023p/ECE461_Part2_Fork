package com.spring_rest_api.api_paths;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.spring_rest_api.api_paths.entity.RegexSchema;
import com.spring_rest_api.api_paths.service.RegexService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;




@RestController
public class PackageByRegexController {
    private final ResponseEntity<String> notFoundError = ResponseEntity.status(HttpStatus.NOT_FOUND).body("No package found under this regex.");

    @Autowired
    RegexService regexService;
    
    @PostMapping("/package/byRegEx")
    public ResponseEntity<String> postMethodName(@RequestBody RegexSchema regexSchema) throws ExecutionException, InterruptedException {
        String result = regexService.getVersionName(regexSchema);
        if (result == null)
            return notFoundError;
        
        return ResponseEntity.ok().body(result);
    }
    

}