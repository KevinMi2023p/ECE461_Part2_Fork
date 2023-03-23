package com.spring_rest_api.api_paths;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PackagesPostController {
    
    @PostMapping("/packages")
    public void packages_plurual() {
        System.out.println("Packages!");
    }

    @PostMapping("/package")
    public void package_single() {
        System.out.println("Package!");
    }

}
