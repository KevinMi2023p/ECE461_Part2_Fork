package com.spring_rest_api.api_paths;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ResetController {
    
    @DeleteMapping("/reset")
    public void reset() {
        System.out.println("Reset!");
    }

}
