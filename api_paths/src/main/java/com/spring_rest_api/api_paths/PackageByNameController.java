package com.spring_rest_api.api_paths;

import org.springframework.web.bind.annotation.RestController;

import com.spring_rest_api.api_paths.service.PackageNameService;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
public class PackageByNameController {
    private final ResponseEntity<String> notFoundError = ResponseEntity.status(HttpStatus.NOT_FOUND).body("Package does not exist.");

    @Autowired
    PackageNameService packageNameService;
    
	@GetMapping("/package/byName/{name}")
	public String packageByName(@PathVariable String name) throws ExecutionException, InterruptedException {
        // Integer res = packageNameService.removeByName(name);
        // System.out.println(res);

		return String.format("PackageByName %s!", name);
	}

    @DeleteMapping("/package/byName/{name}")
    public ResponseEntity<String> deleteMethodName(@PathVariable String name) throws ExecutionException, InterruptedException {
        Integer res = packageNameService.removeByName(name);
        if (res == 0)
            return notFoundError;
        
        String successMsg = "Package is deleted.";
        return ResponseEntity.ok().body(successMsg);
    }

}
