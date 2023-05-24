package com.eliteworks.eliteworks.services;

import com.eliteworks.eliteworks.models.User;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class UserService {

    public String createUser(User user) throws ExecutionException, InterruptedException {
        Firestore firestore = FirestoreClient.getFirestore();
        DocumentReference userRef = firestore.collection("users").document(user.getId());
        ApiFuture<DocumentSnapshot> documentSnapshotApiFuture = userRef.get();
        DocumentSnapshot documentSnapshot = documentSnapshotApiFuture.get();

        if (documentSnapshot.exists()) {
            return "User already exists";
        } else {
            ApiFuture<WriteResult> collectionsApiFuture = userRef.set(user);
            return "User created successfully";
        }
    }


    public User getUser(String id) throws ExecutionException, InterruptedException {
        Firestore firestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = firestore.collection("users").document(id);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot snapshot = future.get();
        User user;
        if(snapshot.exists()){
            user = snapshot.toObject(User.class);
            return user;
        }
        return null;
    }

    public String updateUser(User user) {
        Firestore firestore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> collectionApi = firestore.collection("users").document(user.getId()).set(user);
        return "User Updated Successfully";
    }

    public String deleteUser(String id) {
        Firestore firestore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult>writeResultApiFuture = firestore.collection("users").document(id).delete();
        return "User Deleted - " + id;
    }
}
