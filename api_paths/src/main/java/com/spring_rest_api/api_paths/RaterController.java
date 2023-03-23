package com.spring_rest_api.api_paths;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
public class RaterController {
    
    @GetMapping("/package/{id}/rate")
    public String getMethodName(@PathVariable String id) {
        return String.format("Rater %s!", id);
    }
    

}
