package com.spring_rest_api.api_paths;

import com.spring_rest_api.api_paths.entity.Data;
import com.spring_rest_api.api_paths.entity.Metadata;
import com.spring_rest_api.api_paths.entity.Product;
import com.spring_rest_api.api_paths.entity.encodedProduct;
import com.spring_rest_api.api_paths.service.PackageService;
import com.spring_rest_api.cli.NetScoreMetric;
import com.spring_rest_api.cli.NetScoreUtil;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLOutput;
import java.util.concurrent.ExecutionException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;


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

    @PostMapping(value = "/package", produces = "application/json")
    public ResponseEntity<String> package_single(@RequestBody encodedProduct encode) throws ExecutionException, InterruptedException, IOException {
        //packageService.savePackage(product);

        // Content and URL are both set
        if (encode.getContent() != null && encode.getURL() != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There is missing field(s) in the PackageData/AuthenticationToken or it is formed improperly (e.g. Content and URL are both set), or the AuthenticationToken is invalid.");
        }
        // Content is not set and URL is set
        if (encode.getContent() == null && encode.getURL() != null) {
            NetScoreMetric nsm = NetScoreUtil.GetNetScore(encode.getURL());
            // if condition can change based on score we want to pass
            if (nsm.NetScore <= -1.0)
                return ResponseEntity.status(424).body("Package is not uploaded due to the disqualified rating.");

            String githubUrl = encode.getURL();
            String accessToken = "";

            URL url = new URL(githubUrl + "/archive/refs/heads/master.zip");
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("Authorization", "token " + accessToken);
            connection.connect();

            try (InputStream inputStream = connection.getInputStream()) {
                byte[] data = inputStream.readAllBytes();
                String base64Encoded = Base64.getEncoder().encodeToString(data);
                encode.setContent(base64Encoded);

            }

            byte[] decodedBytes = Base64.getDecoder().decode(encode.getContent());
            Product product = new Product();
            Data data = new Data();
            data.setURL(encode.getURL());
            Metadata metadata = new Metadata();
            int exists = 0;

            try (InputStream is = new ByteArrayInputStream(decodedBytes);
                 ZipInputStream zis = new ZipInputStream(is)) {
//                 GzipCompressorInputStream gzis = new GzipCompressorInputStream(is);
//                 TarArchiveInputStream tais = new TarArchiveInputStream(gzis)) {
                ZipEntry entry;
                //TarArchiveEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    //System.out.println(entry.getName());
                    if (!entry.isDirectory() && entry.getName().endsWith("package.json")) {
                        String jsonContent = IOUtils.toString(zis, "UTF-8");
                        JSONObject jsonObject = new JSONObject(jsonContent);
                        try {
                            String name = jsonObject.getString("name");
                            String version = jsonObject.getString("version");
                            data.setContent(encode.getContent());
                            data.setJSProgram(encode.getJSProgram());
                            if (data.getURL() == null) {
                                String URL = jsonObject.getString("homepage");
                                data.setURL(URL);
                            }
                            metadata.setName(name);
                            metadata.setVersion(version);
                            metadata.setID(name + "-" + version);
                            product.setData(data);
                            product.setMetadata(metadata);
                            exists = 1;
                            break;
                        } catch (JSONException e) {
                            break;
                        }

                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (exists == 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There is missing field(s) in the PackageData/AuthenticationToken or it is formed improperly (e.g. Content and URL are both set), or the AuthenticationToken is invalid.");
            } else {
                String str = packageService.savePackage(product);
                if (str == "Package exists already") {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(str);
                }
                return ResponseEntity.status(HttpStatus.CREATED).body(str);
            }
        }
        // Content is set and URL is not
        else if (encode.getContent() != null && encode.getURL() == null) {
            byte[] decodedBytes = Base64.getDecoder().decode(encode.getContent());
            Product product = new Product();
            Data data = new Data();
            data.setURL(encode.getURL());
            Metadata metadata = new Metadata();
            int exists = 0;

            try (InputStream is = new ByteArrayInputStream(decodedBytes);
                 ZipInputStream zis = new ZipInputStream(is)) {
                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    System.out.println(entry.getName());
                    if (!entry.isDirectory() && entry.getName().endsWith("package.json")) {
                        String jsonContent = IOUtils.toString(zis, "UTF-8");
                        JSONObject jsonObject = new JSONObject(jsonContent);
                        try {
                            String name = jsonObject.getString("name");
                            String version = jsonObject.getString("version");
                            String URL = jsonObject.getString("homepage");
                            data.setContent(encode.getContent());
                            data.setJSProgram(encode.getJSProgram());
                            if (data.getURL() == null) {
                                data.setURL(URL);
                            }
                            metadata.setName(name);
                            metadata.setVersion(version);
                            metadata.setID(name + "-" + version);
                            product.setData(data);
                            product.setMetadata(metadata);
                            exists = 1;
                            break;
                        } catch (JSONException e) {
                            break;
                        }

                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (exists == 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There is missing field(s) in the PackageData/AuthenticationToken or it is formed improperly (e.g. Content and URL are both set), or the AuthenticationToken is invalid.");
            } else {
                String str = packageService.savePackage(product);
                if (str == "Package exists already") {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(str);
                }
                return ResponseEntity.status(HttpStatus.CREATED).body(str);
            }
        }

        return null;
    }
}