package com.example.travelmantics;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirebaseUtil {
    public static FirebaseDatabase mFirebaseDatabase;
    public static DatabaseReference mDatabaseReference;
    private static FirebaseUtil firebaseUtil;
    public static FirebaseAuth firebaseAuth;
    public static FirebaseStorage firebaseStorage;
    public static StorageReference storageReference;
    public static FirebaseAuth.AuthStateListener authStateListener;
    public static ArrayList<TravelDeal> mDeals;
    private static ListActivity caller;
    private static final int RC_SIGN_IN = 123;
    public static Boolean isAdmin;


    private FirebaseUtil() {
    }

    public static void openFbReference(String ref, final ListActivity callerActivity) {
        if (firebaseUtil == null) {
            /*isAdmin = false;added statement to avoid Null Pointer on isAdmin on List activity*/
            firebaseUtil = new FirebaseUtil();
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            firebaseAuth = FirebaseAuth.getInstance();
            caller = callerActivity;

            authStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if(firebaseAuth.getCurrentUser() == null) {
                        FirebaseUtil.signIn();
                    }else {
                        String userId = firebaseAuth.getUid();
                        checkAdmin(userId);
                    }
                    //changes here
                    Toast.makeText( callerActivity.getBaseContext(),"Welcome Back!",Toast.LENGTH_LONG).show();
                }

            };
            connectStorage();
        }
            mDeals = new ArrayList<TravelDeal>();
            mDatabaseReference = mFirebaseDatabase.getReference().child(ref);
        }

    private static void checkAdmin(String userId) {
        FirebaseUtil.isAdmin = false;
        DatabaseReference ref = mFirebaseDatabase.getReference().child("administrators")
                .child(userId);
        ChildEventListener listener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
               FirebaseUtil.isAdmin = true;
               Log.d("admin","You are a admin");
               caller.showMenu();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        ref.addChildEventListener(listener);
    }

    private static void signIn(){
            // Choose authentication providers
            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.EmailBuilder().build(),
                    new AuthUI.IdpConfig.GoogleBuilder().build());

            // Create and launch sign-in intent
            caller.startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            //.setIsSmartLockEnabled(false)
                            .build(),
                    RC_SIGN_IN);
        }



        public static void attachListener () {
            firebaseAuth.addAuthStateListener(authStateListener);
        }
        public static void detachListener () {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }public static void connectStorage(){
            firebaseStorage = FirebaseStorage.getInstance();
            storageReference = firebaseStorage.getReference().child("deals_pictures");
    }


    }


