package com.spring_rest_api.api_paths;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.spring_rest_api.api_paths.entity.RegexSchema;
import com.spring_rest_api.api_paths.service.RegexService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;




@RestController
public class PackageByRegexController {

    @Autowired
    RegexService regexService;
    
    @PostMapping("/package/byRegEx")
    public void postMethodName(@RequestBody RegexSchema regexSchema) throws ExecutionException, InterruptedException {
                
        regexService.getVersionName(regexSchema);

        return;
    }
    

}