package com.spring_rest_api.api_paths;

// import org.springframework.boot.SpringApplication;
// import org.springframework.boot.autoconfigure.SpringBootApplication;

// @SpringBootApplication
// public class ApiPathsApplication {

// 	public static void main(String[] args) {
// 		SpringApplication.run(ApiPathsApplication.class, args);
// 	}

// }

// package com.example.demo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class ApiPathsApplication {
    public static void main(String[] args) {
      SpringApplication.run(ApiPathsApplication.class, args);
    }

	@GetMapping("/packages")
	public String packages(@RequestParam(value = "name", defaultValue = "World") String name) {
		return String.format("Packages %s!", name);
	}

	@GetMapping("/reset")
	public String reset(@RequestParam(value = "name", defaultValue = "World") String name) {
		return String.format("Reset %s!", name);
	}

	// Needs to handle the following:
	// GET, POST, DELETE
	@GetMapping("/package/{id}")
	public String packageId(@RequestParam(value = "name", defaultValue = "World") String name) {
		return String.format("PackageId %s!", name);
	}

	@GetMapping("/package/{id}/rate")
	public String packageIdRate(@RequestParam(value = "name", defaultValue = "World") String name) {
		return String.format("PackageIdRate %s!", name);
	}

	@GetMapping("/authenticate")
	public String authenticate(@RequestParam(value = "name", defaultValue = "World") String name) {
		return String.format("Authenticate %s!", name);
	}

	@GetMapping("/package/byName/{name}")
	public String packageByName(@RequestParam(value = "name", defaultValue = "World") String name) {
		return String.format("PackageByName %s!", name);
	}
}