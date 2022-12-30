package com.example.accountmanage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountListActivity extends SingleFragmentActivity {

    private FirebaseFirestore firestore;


    // variable for our adapter
    // class and array list
    private AccountListFragment.AccountAdapter mAdapter;
    private List<Account> accountList;

    @Override
    protected Fragment createFragment() {
        return new AccountListFragment();
    }



//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//
//        setContentView(R.layout.activity_main);
//
////        firestore = FirebaseFirestore.getInstance();
//
//
//
//        // Create a new HashMap to store the account data
//        Map<String, Object> accounts = new HashMap<>();
//
//        // Create a new Account object with the given username and password
//        Account account = new Account("Facebook","www.facebook.com","kailun","kailun");
//
//        // Add the username and password to the accounts HashMap
//        accounts.put("websiteName",account.getWebsiteName());
//        accounts.put("websiteUrl",account.getWebsiteUrl());
//        accounts.put("username",account.getUsername());
//        accounts.put("password",account.getPassword());
//
//        // Add the accounts HashMap to the "accounts" collection in Firestore
//        firestore.collection("accounts").add(accounts)
//                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                    // If the data was added successfully, show a Toast message
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//                        Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    // If there was an error adding the data, show a Toast message
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(getApplicationContext(),"Failure",Toast.LENGTH_SHORT).show();
//                    }
//                });

//    }






}