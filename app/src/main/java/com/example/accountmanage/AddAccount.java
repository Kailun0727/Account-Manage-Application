package com.example.accountmanage;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.example.accountmanage.databinding.ActivityAddAccountBinding;
import com.example.accountmanage.databinding.FragmentAccountsBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddAccount extends AppCompatActivity {

    private ActivityAddAccountBinding mBinding;

    private AccountListFragment.AccountAdapter mAdapter;

    private FragmentAccountsBinding mAccountsBinding;

    private List<Account> mAccountList;



    //database
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivityAddAccountBinding.inflate(getLayoutInflater());

        setContentView(mBinding.getRoot());



        //add text watcher to edit text
        mBinding.websiteNameEditText.addTextChangedListener(textWatcher);
        mBinding.websiteUrlEditText.addTextChangedListener(textWatcher);
        mBinding.usernameEditText.addTextChangedListener(textWatcher);
        mBinding.passwordEditText.addTextChangedListener(textWatcher);

        mBinding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                // Get a reference to the SharedPreferences object
                SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);

                // If the key exists, get the value of the counter from the SharedPreferences object
                int counter_sp = sharedPreferences.getInt("counter", 0);

                String id = "account_"+String.valueOf(counter_sp);

                //use setResult for Result Launcher Callback function to work
                setResult(RESULT_OK);

                String websiteName = mBinding.websiteNameEditText.getText().toString();
                String websiteUrl = mBinding.websiteUrlEditText.getText().toString();
                String username = mBinding.usernameEditText.getText().toString();
                String password = mBinding.passwordEditText.getText().toString();

                firestore = FirebaseFirestore.getInstance();

                CollectionReference accountsRef = firestore.collection("accounts");


                // Create a new Account object with the given username and password
                Account account = new Account(id,websiteName,websiteUrl,username,password);

                // Create a new HashMap to store the account data
                Map<String, Object> accounts = new HashMap<>();

                // Add the username and password to the accounts HashMap
                accounts.put("id",account.getId());
                accounts.put("websiteName",account.getWebsiteName());
                accounts.put("websiteUrl",account.getWebsiteUrl());
                accounts.put("username",account.getUsername());
                accounts.put("password",account.getPassword());

                //  Add the accounts HashMap to the "accounts" collection by using custom id in Firestore
                accountsRef.document(id).set(accounts).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(),"Account added successfully!",Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"Account added failed!",Toast.LENGTH_SHORT).show();
                    }
                });

                // Increment the counter by one
                counter_sp++;

                // Get a reference to the SharedPreferences.Editor object
                SharedPreferences.Editor editor = sharedPreferences.edit();

                // Put the updated value of the counter into the SharedPreferences object
                editor.putInt("counter",counter_sp);

                // Save the changes to the SharedPreferences object
                editor.commit();



                }
            });
    }



    //text watcher to check the edit text is empty or not
    public TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String websiteName = mBinding.websiteNameEditText.getText().toString();
            String websiteUrl = mBinding.websiteUrlEditText.getText().toString();
            String username = mBinding.usernameEditText.getText().toString();
            String password = mBinding.passwordEditText.getText().toString();

            //if all field is not empty , then set the button to enabled
            if (!websiteName.isEmpty() && !websiteUrl.isEmpty() && !username.isEmpty() && !password.isEmpty()){
                mBinding.btnSave.setEnabled(true);
            }

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };



}