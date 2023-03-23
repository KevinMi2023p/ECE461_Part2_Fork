package com.spring_rest_api.api_paths;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
public class AuthenticateController {
    
    @PutMapping("/authenticate")
    public void putMethodName() {
        System.out.println("Authenticate!");
    }

}
