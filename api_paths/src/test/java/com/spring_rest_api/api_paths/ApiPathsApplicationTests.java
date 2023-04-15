package com.spring_rest_api.api_paths;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;

@SpringBootTest
@AutoConfigureMockMvc
class ApiPathsApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

	// check if the endpoint is get or not
    // @Test
    // void testGetPackages() throws Exception {
    //     mockMvc.perform(MockMvcRequestBuilders.get("/packages"))
    //             .andExpect(MockMvcResultMatchers.status().isOk());
    // }

    @Test
    void testResetRegistry() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/reset"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testGetPackageById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/package/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }


	// This is a trial package
//     @Test
// 	void testUpdatePackageById() throws Exception {
//     String id = "1";
//     String requestBody = "{\"id\":1,\"name\":\"TestPackage\",\"version\":\"1.0\",\"description\":\"This is a test package\"}";

//     mockMvc.perform(MockMvcRequestBuilders.put("/package/" + id)
//             .contentType(MediaType.APPLICATION_JSON)
//             .content(requestBody))
//             .andExpect(MockMvcResultMatchers.status().isOk());
// }

    @Test
    void testDeletePackageById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/package/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testGetPackageRatingById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/package/1/rate"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testAuthenticate() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/authenticate"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testGetPackageByName() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/package/byName/TestPackage"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testDeletePackageByName() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/package/byName/TestPackage"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

	// Check if this is post
    // @Test
    // void testGetPackageByRegEx() throws Exception {
    //     mockMvc.perform(MockMvcRequestBuilders.post("/package/byRegEx")
    //                     .contentType(MediaType.APPLICATION_JSON)
    //                     .content(objectMapper.writeValueAsString(Arrays.asList("TestPackage"))))
    //             .andExpect(MockMvcResultMatchers.status().isOk());
    // }

}
