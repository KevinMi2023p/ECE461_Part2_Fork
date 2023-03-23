package com.spring_rest_api.api_paths;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
public class PackageIdController {
    
	@GetMapping("/package/{id}")
	public String packageId(@PathVariable String id) {
		return String.format("PackageId %s!", id);
	}

    @PutMapping("/package/{id}")
    public void putMethodName(@PathVariable String id) {
        System.out.println("Put! %s" + id);
    }

    @DeleteMapping("/package/{id}")
    public void deleteMethodName(@PathVariable String id) {
        System.out.println("Delete! %s" + id);
    }

}
