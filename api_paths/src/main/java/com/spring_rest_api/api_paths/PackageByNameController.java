package com.spring_rest_api.api_paths;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
public class PackageByNameController {
    
	@GetMapping("/package/byName/{name}")
	public String packageByName(@PathVariable String name) {
		return String.format("PackageByName %s!", name);
	}

    @DeleteMapping("/package/byName/{name}")
    public void deleteMethodName(@PathVariable String name) {
        System.out.println("Delete!");
    }

}
