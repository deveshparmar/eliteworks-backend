package com.eliteworks.eliteworks.services;

import com.eliteworks.eliteworks.models.User;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
public class UserService {

    private static final long EXPIRE_TOKEN_AFTER_MINUTES = 10;

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

    public String forgotPassword(String email) throws ExecutionException, InterruptedException {
        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference usersCollection = firestore.collection("users");

        Query query = usersCollection.whereEqualTo("email",email).limit(1);
        ApiFuture<QuerySnapshot> querySnapshotFuture = query.get();

        QuerySnapshot querySnapshot = querySnapshotFuture.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();

        if (!documents.isEmpty()) {
            DocumentSnapshot documentSnapshot = documents.get(0);
            User existingUser = documentSnapshot.toObject(User.class);
            existingUser.setToken(generateToken());
            existingUser.setTokenDate(LocalDateTime.now().toString());
            updateUser(existingUser);
            return existingUser.getToken();
        } else {
            return "User does not exist";
        }
    }

    private String generateToken() {

        return UUID.randomUUID() +
                UUID.randomUUID().toString();
    }

    private boolean isTokenExpired(final LocalDateTime tokenCreationDate) {

        LocalDateTime now = LocalDateTime.now();
        Duration diff = Duration.between(tokenCreationDate, now);

        return diff.toMinutes() >= EXPIRE_TOKEN_AFTER_MINUTES;
    }

    public String resetPassword(String token, String id,String password) throws ExecutionException, InterruptedException {
        id = id.split(",")[0];
        token = token.split(",")[0];
        password = password.split(",")[0];
        User user = getUserByToken(token);
        User user1 = getUser(id);
        System.out.println("ID - " + id + " Token - " + token +" User1 - " + user1);
        System.out.println("User - " + user);
        if(user==null){
            return "Invalid Token";
        }
        String str = user.getTokenDate();
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS");
        LocalDateTime dateTime = LocalDateTime.parse(str, inputFormatter);
//        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
//        String formattedDateTime = dateTime.format(outputFormatter);


        if(isTokenExpired(dateTime)){
            return "Token expired";
        }
        user.setPassword(password);
        user.setToken(null);
        user.setTokenDate(null);
        updateUser(user);
        return "Success";
    }

    public User getUserByToken(String token) throws ExecutionException, InterruptedException {
        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference usersCollection = firestore.collection("users");
        Query query = usersCollection.whereEqualTo("token", token).limit(1);
        ApiFuture<QuerySnapshot> querySnapshotFuture = query.get();
        User user;
        QuerySnapshot querySnapshot = querySnapshotFuture.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();

        if (!documents.isEmpty()) {
            DocumentSnapshot documentSnapshot = documents.get(0);
            return documentSnapshot.toObject(User.class);
        } else {
            return null;
        }
    }

    public String getIdOfUser(String email) throws ExecutionException, InterruptedException {
        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference usersCollection = firestore.collection("users");
        Query query = usersCollection.whereEqualTo("email", email).limit(1);
        ApiFuture<QuerySnapshot> querySnapshotFuture = query.get();
        User user;
        QuerySnapshot querySnapshot = querySnapshotFuture.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();

        if (!documents.isEmpty()) {
            DocumentSnapshot documentSnapshot = documents.get(0);
           user = documentSnapshot.toObject(User.class);
            return user.getId();
        } else {
            return null;
        }
    }



}
