package com.buzzware.apnigari.Firebase;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseInstances {

    public static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
//    public static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance().gete;

    public static CollectionReference bookingsCollection = firebaseFirestore.collection("Bookings");
    public static CollectionReference usersCollection = firebaseFirestore.collection("Users");
    public static CollectionReference emergencyCollection = firebaseFirestore.collection("Emergency");
    public static CollectionReference scheduledRidesCollection = firebaseFirestore.collection("ScheduledRides");
    public static CollectionReference chatCollection = firebaseFirestore.collection("Chat");

    public static CollectionReference adminChatCollection = firebaseFirestore.collection("AdminChat");
}
