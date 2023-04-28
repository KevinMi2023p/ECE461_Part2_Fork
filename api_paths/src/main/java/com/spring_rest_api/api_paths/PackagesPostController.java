package com.spring_rest_api.api_paths;

import com.spring_rest_api.api_paths.entity.Data;
import com.spring_rest_api.api_paths.entity.Metadata;
import com.spring_rest_api.api_paths.entity.Product;
import com.spring_rest_api.api_paths.entity.encodedProduct;
import com.spring_rest_api.api_paths.service.PackageService;
import org.json.JSONException;
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
        int exists = 0;

        try (InputStream is = new ByteArrayInputStream(decodedBytes);
             ZipInputStream zis = new ZipInputStream(is)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().equals("package.json")) {
                    String jsonContent = IOUtils.toString(zis, "UTF-8");
                    JSONObject jsonObject = new JSONObject(jsonContent);
                    try {
                        String name = jsonObject.getString("name");
                        String version = jsonObject.getString("version");
//                        String URL = jsonObject.getString("URL");
                        data.setContent(encode.getContent());
                        data.setJSProgram(encode.getJSProgram());
//                        data.setURL(URL);
                        metadata.setName(name);
                        metadata.setVersion(version);
                        metadata.setID(name + "-" + version);
                        product.setData(data);
                        product.setMetadata(metadata);
                        exists = 1;
                        break;
                    }
                    catch (JSONException e){
                        break;
                    }

                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if(exists == 0){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There is missing field(s) in the PackageData/AuthenticationToken or it is formed improperly (e.g. Content and URL are both set), or the AuthenticationToken is invalid.");
        }
        else{
            String str = packageService.savePackage(product);
            if(str == "Package exists already"){
                return ResponseEntity.status(HttpStatus.CONFLICT).body(packageService.savePackage(product));
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(packageService.savePackage(product));
        }

    }

}