package com.spring_rest_api.api_paths;

import com.google.api.gax.rpc.InternalException;
import com.google.common.collect.ArrayListMultimap;
import com.google.gson.Gson;
import com.spring_rest_api.api_paths.entity.Data;
import com.spring_rest_api.api_paths.entity.Metadata;
import com.spring_rest_api.api_paths.entity.PagQuery;
import com.spring_rest_api.api_paths.entity.Product;
import com.spring_rest_api.api_paths.entity.encodedProduct;
import com.spring_rest_api.api_paths.service.AuthenticateService;
import com.spring_rest_api.api_paths.service.PackageService;
import com.spring_rest_api.api_paths.service.PackagesQueryService;

// import io.netty.handler.timeout.TimeoutException;
// import io.netty.util.concurrent.Future;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

// import org.apache.commons.compress.harmony.pack200.NewAttributeBands.Callable;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;


@RestController
public class PackagesPostController {
    private final ResponseEntity<String> badRequestError = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There is missing field(s) in the PackageID/AuthenticationToken or it is formed improperly, or the AuthenticationToken is invalid.");
    private final ResponseEntity<String> tooLargeError = ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body("Too many packages returned.");

    private final Logger logger;

    @Autowired
    private PackageService packageService;

    @Autowired
    private PackagesQueryService packagesQueryService;

    @Autowired
    AuthenticateService authenticateService;

    public PackagesPostController() {
        this.logger = LoggerFactory.getLogger(this.getClass());
    }
    
    @PostMapping(value = "/packages", produces = "application/json")
    public ResponseEntity<?> packages_plurual(@RequestBody List<PagQuery> pagQuerys, @RequestHeader("X-Authorization") String token, @RequestParam(name = "offset", required = false) String offsetVar) throws ExecutionException, InterruptedException {
        int offsetSize = 5;
        
        if (!authenticateService.validateAuthHeaderForUser(token)) {
            logger.info("Bad token.");
            return badRequestError;
        }
        int offset;
        if (offsetVar != null) {
            offset = packagesQueryService.checkValidQueryVar(offsetVar);
        } else {
            offset = 0;
        }

        if (offset < 0) {
            logger.info("Negative offset: {}", offset);
            return badRequestError;
        }

        if (!packagesQueryService.checkValidQuery(pagQuerys)) {
            logger.info("Bad queries.");
            return badRequestError;
        }
        // Above sections check if the parameters for the function are correct


        // Section below will run the queries, if it takes longer than 5 seconds it times out
        ExecutorService executor = Executors.newCachedThreadPool();
        Callable<ArrayList<Map<String,Object>>> task = new Callable<ArrayList<Map<String,Object>>>() {
            public ArrayList<Map<String,Object>> call() throws ExecutionException, InterruptedException {
                return packagesQueryService.pagnitatedqueries(pagQuerys);
            }
        };
        Future<ArrayList<Map<String,Object>>> future = executor.submit(task);
        ArrayList<Map<String,Object>> result;
        try {
            result = future.get(5, TimeUnit.SECONDS);
        } catch (TimeoutException ex) {
            return tooLargeError;
        }

        logger.info("Result size: ", result.size());

        if (offset >= result.size()) {
            result = new ArrayList<Map<String, Object>>();
        } else {
            result = new ArrayList<Map<String, Object>>(result.subList(offsetSize * offset, result.size()));
        }
        
        if (result.size() > offsetSize) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("offset", String.format("{}", offset + 1));
            result = new ArrayList<Map<String, Object>>(result.subList(0, offsetSize));
            return new ResponseEntity<ArrayList<Map<String, Object>>>(result, headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<ArrayList<Map<String, Object>>>(result, HttpStatus.OK);
        }
    }

    @PostMapping(value = "/package", produces = "application/json")
    public ResponseEntity<String> package_single(@RequestBody encodedProduct encode ,  @RequestHeader("X-Authorization") String token) throws ExecutionException, InterruptedException , MalformedURLException, IOException {
        if (!authenticateService.validateAuthHeaderForUser(token)) {
            return badRequestError;
        }
        System.out.println("encode URL " + encode.URL + " encode content " + encode.Content + "encode jsprogram" + encode.JSProgram);
        logger.debug("Token Value: {}",token);
        logger.debug("URL value: {}",encode.URL);
        //packageService.savePackage(product);

        // Neither Content and URL are set
        if (encode.getContent() == null && encode.getURL() == null) {
            System.out.println("encode URL " + encode.URL + " encode content " + encode.Content + "encode jsprogram" + encode.JSProgram);
            return badRequestError;
        }

        // Content and URL are both set
        if (encode.getContent() != null && encode.getURL() != null) {
            System.out.println("encode URL " + encode.URL + " encode content " + encode.Content + "encode jsprogram" + encode.JSProgram);
            return badRequestError;
        }
        // Content is not set and URL is set
        if (encode.getContent() == null && encode.getURL() != null) {
            String githubUrl = encode.getURL();
            String accessToken = readAccessTokenFromFile();
            logger.debug("Github Access Token: {}",accessToken);

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
                    String entry_name = entry.getName();
                    int valid_file = entry_name.length() - entry_name.replace("/","").length();
                    //if (!entry.isDirectory() && entry.getName().endsWith("package.json"))
                    if (valid_file == 1 && entry.getName().endsWith("package.json")){
                        String jsonContent = IOUtils.toString(zis, "UTF-8");
                        JSONObject jsonObject = new JSONObject(jsonContent);
                        //System.out.println(jsonObject.toString(2));
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
                return badRequestError;
            } else {
                System.out.println(product.getData().getContent().length());
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
                    //System.out.println(entry.getName());
                    String entry_name = entry.getName();
                    int valid_file = entry_name.length() - entry_name.replace("/","").length();
                    if (valid_file == 1 && entry.getName().endsWith("package.json")) {
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
                return badRequestError;
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



    private static String readAccessTokenFromFile() throws IOException {
        ClassPathResource resource = new ClassPathResource("githubToken.txt");
        try (InputStream inputStream = resource.getInputStream()) {
            byte[] bytes = inputStream.readAllBytes();
            return new String(bytes, StandardCharsets.UTF_8).trim();
        }
    }


}