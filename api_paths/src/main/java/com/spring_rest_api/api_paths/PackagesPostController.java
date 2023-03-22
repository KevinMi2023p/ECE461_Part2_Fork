package com.spring_rest_api.api_paths;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PackagesPostController {
    
    @PostMapping("/packages")
    public String packages() {
        return "Packages!";
    }

    @PostMapping("/package")
    public String packageID() {
        return "Package!";
    }

}
