package com.spring_rest_api.api_paths;


import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import com.spring_rest_api.api_paths.service.ResetService;

import com.spring_rest_api.api_paths.service.AuthenticateService;

@RestController
public class ResetController {

    private final ResponseEntity<String> unAuthError = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You do not have permission to reset the registry.");
    private final ResponseEntity<String> badRequestError = ResponseEntity.badRequest().body("There is missing field(s) in the AuthenticationToken or it is formed improperly, or the AuthenticationToken is invalid.");

    @Autowired
    AuthenticateService authenticateService;

    @Autowired
    ResetService resetService;


    @DeleteMapping(value = "/reset")
    public ResponseEntity<String> reset(@RequestHeader("X-Authorization") String token) {
        try {
            if(validateToken(token) == false) 
                return badRequestError;

            if (resetService.checkAdminToken(token) == false)
                return unAuthError;
            
            boolean result = resetService.clearCollection();
            if (result == false)
                return badRequestError;

            String successMsg = "Registry is reset.";
            return ResponseEntity.ok(successMsg);
        } catch (Exception e) {
            return badRequestError;
        }
    }

    private boolean validateToken(String token) {
        try {
            return authenticateService.validateJwtToken(token);
        } catch (Exception e) {
            return false;
        }
    }

}
