package com.spring_rest_api.api_paths;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;




@RestController
public class PackageByRegexController {
    
    @PostMapping("/package/byRegEx")
    public void postMethodName() {
                
        System.out.println("Starting new api");

        return;
    }
    

}