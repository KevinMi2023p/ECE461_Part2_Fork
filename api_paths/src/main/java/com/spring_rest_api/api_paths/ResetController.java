package com.spring_rest_api.api_paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;
import com.spring_rest_api.api_paths.service.ResetService;

@RestController
public class ResetController {
    @Autowired
    ResetService resetService;

    // Reset to the default system state (an empty registry with the default user)
    @DeleteMapping("/reset")
    public ResponseEntity<String> reset() {
        resetService.resetAll();
        return ResponseEntity.status(HttpStatus.OK).body("System reset successfully.");
    }
}
