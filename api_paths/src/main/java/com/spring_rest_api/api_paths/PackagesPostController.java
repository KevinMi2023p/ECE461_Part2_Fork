package com.spring_rest_api.api_paths;

import com.spring_rest_api.api_paths.entity.Data;
import com.spring_rest_api.api_paths.entity.Metadata;
import com.spring_rest_api.api_paths.entity.Product;
import com.spring_rest_api.api_paths.entity.encodedProduct;
import com.spring_rest_api.api_paths.service.PackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

@RestController
public class PackagesPostController {

    private final Logger logger;

    @Autowired
    private PackageService packageService;

    public PackagesPostController() {
        this.logger = LoggerFactory.getLogger(this.getClass());
    }
    
    @PostMapping("/packages")
    public void packages_plurual() {
        System.out.println("Packages!");
    }

    @PostMapping("/package")
    public ResponseEntity<String> package_single(@RequestBody encodedProduct encode) throws ExecutionException, InterruptedException {
        //packageService.savePackage(product);
        byte[] decodedBytes = Base64.getDecoder().decode(encode.getContent());
        Product product = new Product();
        Data data = new Data();
        Metadata metadata = new Metadata();

        try (InputStream is = new ByteArrayInputStream(decodedBytes);
             ZipInputStream zis = new ZipInputStream(is)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().equals("package.json")) {
                    String jsonContent = IOUtils.toString(zis, "UTF-8");
                    JSONObject jsonObject = new JSONObject(jsonContent);
                    String name = jsonObject.getString("name");
                    String version = jsonObject.getString("version");
                    data.setContent(encode.getContent());
                    data.setJSProgram(encode.getJSProgram());
                    metadata.setName(name);
                    metadata.setVersion(version);
                    metadata.setID(name + "-" + version);
                    product.setData(data);
                    product.setMetadata(metadata);
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return ResponseEntity.status(HttpStatus.CREATED).body(packageService.savePackage(product));

    }

}