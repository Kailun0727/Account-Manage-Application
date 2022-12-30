package com.example.accountmanage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.accountmanage.databinding.ActivityUpdateAccountBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class UpdateAccount extends AppCompatActivity {

    private FirebaseFirestore firestore;


    private ActivityUpdateAccountBinding mBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivityUpdateAccountBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());


        mBinding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                firestore = FirebaseFirestore.getInstance();

                //Get the data from intent to compare with firebase data by using where
                Intent i = getIntent();
                String id = i.getStringExtra("id");



                Log.i("LOGGER","document Id : "+id);

                DocumentReference docRef = firestore.collection("accounts").document(id);

                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();

                            if (document != null) {
                                Log.i("LOGGER","id "+document.getString("id"));
                                Log.i("LOGGER","Username "+document.getString("username"));
                                Log.i("LOGGER","Password "+document.getString("password"));
                                Log.i("LOGGER","Website Name "+document.getString("websiteName"));
                                Log.i("LOGGER","Website Url "+document.getString("websiteUrl"));
                            } else {
                                Log.d("LOGGER", "No such document");
                            }

                            //Get the string from edit text
                            String newWebsiteName = mBinding.websiteNameEditText.getText().toString();
                            String newWebsiteUrl = mBinding.websiteUrlEditText.getText().toString();
                            String newUsername = mBinding.usernameEditText.getText().toString();
                            String newPassword = mBinding.passwordEditText.getText().toString();

                            boolean allowUpdate = true;


                            // Update the document with the new field values
                            Map<String, Object> updates = new HashMap<>();

                            // At all EditText fields is empty
                            if (newWebsiteName.isEmpty() &&
                                    newWebsiteUrl.isEmpty() &&
                                    newUsername.isEmpty() &&
                                    newPassword.isEmpty()) {
                                allowUpdate = false;

                            }

                            // At least one of the EditText fields is not empty
                            if (!newWebsiteName.isEmpty() &&
                                    newWebsiteUrl.isEmpty() &&
                                    newUsername.isEmpty() &&
                                    newPassword.isEmpty()
                            ) {
                                updates.put("websiteName", newWebsiteName);
                                docRef.set(updates, SetOptions.merge());
                            }

                            if (!newWebsiteUrl.isEmpty() &&
                                    newWebsiteName.isEmpty() &&
                                    newUsername.isEmpty() &&
                                    newPassword.isEmpty()
                            ) {
                                updates.put("websiteUrl", newWebsiteUrl);
                                docRef.set(updates, SetOptions.merge());
                            }

                            if (!newUsername.isEmpty() &&
                                    newWebsiteName.isEmpty() &&
                                    newWebsiteUrl.isEmpty() &&
                                    newPassword.isEmpty()
                            ) {
                                updates.put("username", newUsername);
                                docRef.set(updates, SetOptions.merge());
                            }


                            if (!newPassword.isEmpty() &&
                                    newWebsiteName.isEmpty() &&
                                    newWebsiteUrl.isEmpty() &&
                                    newUsername.isEmpty()
                            ) {
                                updates.put("password", newPassword);
                                docRef.set(updates, SetOptions.merge());
                            }

                            //If all of the EditText fields is not empty
                            if (!newWebsiteName.isEmpty() &&
                                    !newWebsiteUrl.isEmpty() &&
                                    !newUsername.isEmpty() &&
                                    !newPassword.isEmpty()) {
                                updates.put("websiteName", newWebsiteName);
                                updates.put("websiteUrl", newWebsiteUrl);
                                updates.put("username", newUsername);
                                updates.put("password", newPassword);
                                docRef.update(updates);
                            }

                            System.out.println("Allow update not working");
                            if (allowUpdate == true) {

                                Toast.makeText(getApplicationContext(), "Updated successfully!", Toast.LENGTH_SHORT).show();

                                setResult(RESULT_OK);
                            } else {
                                Toast.makeText(getApplicationContext(), "Update failed!", Toast.LENGTH_SHORT).show();
                            }


                        } else {
                            Log.d("LOGGER", "get failed with ", task.getException());
                        }
                    }
                });








            }


        });
    }
}






