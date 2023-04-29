package com.spring_rest_api.api_paths;

import java.util.Map;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.spring_rest_api.api_paths.service.PackageIdService;
import com.spring_rest_api.cli.NetScoreMetric;
import com.spring_rest_api.cli.NetScoreUtil;


@RestController
public class RaterController {
    private final Logger logger;

    @Autowired
    PackageIdService packageIdService;

    public RaterController() {
        this.logger = LoggerFactory.getLogger(this.getClass());
    }
    
    @GetMapping(value="/package/{id}/rate", produces="application/json")
    public ResponseEntity<Object> PackageRate(@PathVariable String id) {
        Map<String, Object> packageData = null;

        try {
            packageData = packageIdService.getPackageData(id);
        } catch (Exception e) {
            this.logger.info(e.getMessage());
        }
        
        if (packageData == null) {
            String resultMessage = String.format("Could not find package data associated with id = \"%s\"", id);
            this.logger.info(resultMessage);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Package does not exist.");
        }
        
        String packageUrl = (String) packageData.get("URL");

        if (packageUrl == null) {
            String resultMessage = String.format("Package with id = \"%s\" did not have a URL", id);
            this.logger.info(resultMessage);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("The package rating system choked on at least one of the metrics.");
        }
        
        NetScoreMetric nsm = NetScoreUtil.GetNetScore(packageUrl);
        
        if (nsm == null) {
            String resultMessage = String.format("Package with id = \"%s\" and URL = \"%s\" returned a null NetScoreMetric", id, packageUrl);
            this.logger.info(resultMessage);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("The package rating system choked on at least one of the metrics.");
        }

        return new ResponseEntity<Object>(nsm, null, HttpStatus.OK);
    }
    

}
