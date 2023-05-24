package com.eliteworks.eliteworks.services;

import com.eliteworks.eliteworks.models.User;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.List;
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
            return user.getId();
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

    public String loginUser(User user) throws ExecutionException, InterruptedException {
        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference usersCollection = firestore.collection("users");

        Query query = usersCollection.whereEqualTo("email", user.getEmail()).limit(1);
        ApiFuture<QuerySnapshot> querySnapshotFuture = query.get();

        QuerySnapshot querySnapshot = querySnapshotFuture.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();

        if (!documents.isEmpty()) {
            DocumentSnapshot documentSnapshot = documents.get(0);
            User existingUser = documentSnapshot.toObject(User.class);
            if (existingUser.getPassword().equals(user.getPassword())) {
                return existingUser.getId();
            } else {
                return "Invalid credentials";
            }
        } else {
            return "User does not exist";
        }
    }

}
