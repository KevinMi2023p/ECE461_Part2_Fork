package com.spring_rest_api.api_paths.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import com.spring_rest_api.api_paths.entity.PagQuery;

// import ch.qos.logback.core.boolex.Matcher;

@Service
public class PackagesQueryService {
    private final String COLLECTION_NAME = "Packages";
    private final String NameField = "metadata.Name";
    private final String VersionField = "metadata.Version";
    private CollectionReference collectionReference = FirestoreClient.getFirestore().collection(COLLECTION_NAME);

    private int type_of_query(String version_query) {
        String types[] = {"Exact", "Bounded range", "Carat", "Tilde"};
        for (int i = 0; i < types.length; i++) {
            Pattern pattern = Pattern.compile(types[i]);
            Matcher match = pattern.matcher(version_query);
            if (match.find()) {
                return i;
            }
        }
        return -1;
    }

    private List<String> get_nums_from_string(String v_query) {
        List<String> result = new ArrayList<String>();

        // Why double backslash on this regex
        // https://stackoverflow.com/questions/22218350/invalid-escape-sequence-valid-ones-are-b-t-n-f-r-in-java
        Pattern pattern = Pattern.compile("[0-9]\\.[0-9]\\.[0-9]");
        Matcher match = pattern.matcher(v_query);
        while (match.find()) {
            result.add(match.group());
        }

        return result;
    }

    public List<Map<String, Object>> pagnitatedqueries(List<PagQuery> pagQuerys) throws ExecutionException, InterruptedException {
        // Note, there is no OR query for Java on Firestore

        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

        for(PagQuery pags : pagQuerys) {
            Query query = collectionReference.whereEqualTo(NameField, pags.get_Name());
            String version_query = pags.get_Version();

            List<String> nums_found = this.get_nums_from_string(version_query);

            if (nums_found.size() == 1) {
                query = query.whereEqualTo(VersionField, nums_found.get(0));
            } else if (nums_found.size() == 2) {
                query = query.whereEqualTo(VersionField, nums_found.get(0) + "-" + nums_found.get(1));
            } else {
                // invalid request
                return null;
            }
            
            ApiFuture<QuerySnapshot> future = query.get();
            for (DocumentSnapshot document : future.get().getDocuments()) {
                Map<String,Object> metaData = (Map<String, Object>) document.getData().get("metadata");
                result.add(metaData);
            }
            // System.out.println("------new pq------");
        }

        return result;
    }
}
