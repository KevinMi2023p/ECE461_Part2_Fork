package com.spring_rest_api.api_paths.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.internal.NonNull;
import com.google.gson.Gson;
import com.spring_rest_api.api_paths.entity.LinkedList;
import com.spring_rest_api.api_paths.entity.Product;

import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import java.util.Map;

import java.util.concurrent.ExecutionException;



@Service

@DependsOn("firestoreInitialization")
public class PackageService extends DbCollectionService {
    @NonNull
    private static final String COLLECTION_NAME = "Packages";

    @NonNull
    protected String getCollectionName() {
        return COLLECTION_NAME;
    }

    public PackageService() {
        super();
    }

    // We will write code here that will be called in the contollers to send data to the db
    public String savePackage(Product product) throws ExecutionException, InterruptedException {

        Gson gson = new Gson();

        // get document reference
        DocumentReference docRef = this.collectionRef.document(product.getMetadata().getID());
        DocumentReference subDocRef = null;

        CollectionReference subcollectionRef = docRef.collection("linked_list");

        Boolean is_linkedList = false;

        int curr = 1;





        String content = product.getData().getContent();
        int total_size = content.length();
        String new_content = new String(content);

        // get snapshot of document reference
        DocumentSnapshot docSs = docRef.get().get();
        DocumentSnapshot docsss;

        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        if(document.exists()){
            return "Package exists already";
        }
        // collection write result
        ApiFuture<WriteResult> transactionPromise = null;
        ApiFuture<WriteResult> docSnapshot = null;

        // overwrite previous value, if necessary
        if(docSs.exists()) {

        } else{

            if(total_size > 1000000) {
                //product.getData().setContent("");
                String buffer = new_content.substring(0,700000);
                product.getData().setContent(buffer);
                transactionPromise = docRef.set(product);
                int i = 0;
                LinkedList ls = new LinkedList();
                int prev = 0;
                is_linkedList = true;
                while (total_size != 0) {

                    if (total_size > 1000000) {
                        String sub_1 = new_content.substring(i, i + 1000000);
                        total_size = total_size - 1000000;
                        ls.setContext(sub_1);
                        if (i == 0) {
                            ls.setNext(null);
                        } else {
                            ls.setNext(Integer.toString(prev));
                        }
                        i = i + 1000000;
                        subDocRef = subcollectionRef.document(Integer.toString(curr));
                        docSnapshot = subDocRef.set(ls);
                        prev = curr;
                        curr += 1;
                    } else if (total_size > 0 && total_size < 1000000) {
                        String sub_1 = new_content.substring(i, total_size + i);
                        total_size = 0;
                        ls.setContext(sub_1);
                        ls.setNext(Integer.toString(prev));
                        subDocRef = subcollectionRef.document(Integer.toString(curr));
                        docSnapshot = subDocRef.set(ls);

                    }

                }
            }else{
                transactionPromise = docRef.set(product);
            }

        }
        
        if (transactionPromise != null) {
            // wait for collection to update
            transactionPromise.get();
            
            // update the doc snapshot
            docSs = docRef.get().get();
        }

        if(is_linkedList){
            if(docSnapshot != null){
                docSnapshot.get();

                docsss = subDocRef.get().get();
                System.out.println(docsss);
            }
//            Iterable<CollectionReference> sub_collections =
//                    docRef.listCollections();
            String current_ID = Integer.toString(curr);
            System.out.println(current_ID);
            String overall_content = "";
            while(current_ID != null){
                DocumentReference docReff = this.collectionRef.document(product.getMetadata().getID()).collection("linked_list").document(current_ID);
                DocumentSnapshot doc = docReff.get().get();
                String data = doc.getString("context");
                overall_content = data + overall_content;
                current_ID = doc.getString("next");
            }

            //System.out.println(overall_content);
            Map<String,Object> database_data = docSs.getData();
            System.out.println(database_data.get("metadata"));
            Map<String,Object> database_nested = (Map<String,Object>) database_data.get("data");
            database_nested.replace("Content",overall_content);
            return gson.toJson(database_data);
        }

        // return json form of current value in database
        return gson.toJson(docSs.getData());
    }
}
    