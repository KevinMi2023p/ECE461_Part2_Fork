package com.spring_rest_api.api_paths.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Filter;
import com.google.cloud.firestore.Query;
import com.google.firebase.cloud.FirestoreClient;
import com.spring_rest_api.api_paths.entity.PagQuery;

@Service
public class PackagesQueryService {
    private final String COLLECTION_NAME = "Packages";
    private final String fieldToCheck = "metadata.Name";
    private CollectionReference collectionReference = FirestoreClient.getFirestore().collection(COLLECTION_NAME);


    public String pagnitatedqueries(List<PagQuery> pagQuerys) throws ExecutionException, InterruptedException {
        // String name_match = pagQuery.get_Name();
        // // String queries[] = pagQuery.get_Version().split("\\r?\\n|\\r"); piazza says that it's just examples of what it could be, not multiple here

        // There is no or query for java
        // java for andriod doesn't seem to work unfortunatley
        // Query query = collectionReference.where(Filter.or(
        //     Filter.equalTo(fieldToCheck, name_match),
        //     Filter.equalTo("metadata.Version", "1.2.3")
        // ));

        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

        for(PagQuery pq : pagQuerys) {
            // System.out.println(pq.get_Name() + " " + pq.get_Version());
            


        }

        return "";
    }
}
