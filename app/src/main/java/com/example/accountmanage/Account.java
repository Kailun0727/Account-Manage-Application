package com.example.accountmanage;

import android.content.Context;
import android.content.SharedPreferences;

public class Account {

    private String id;
    private String websiteName;
    private String websiteUrl;
    private String username;
    private String password;


    public Account(String id,String websiteName, String websiteUrl, String username, String password){
//        // Get a reference to the SharedPreferences object
//        SharedPreferences sharedPreferences = context.getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
//
//        // Check if the SharedPreferences object contains the "counter" key
//        if (sharedPreferences.contains("counter")){
//            // If the key exists, get the value of the counter from the SharedPreferences object
//            int counter_sp = sharedPreferences.getInt("counter", 0);
//
//            //Increase counter by 1
//            this.counter = counter_sp + 1;
//
//            // Get a reference to the SharedPreferences.Editor object
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//
//            // Put the updated value of the counter into the SharedPreferences object
//            editor.putInt("counter", this.counter);
//
//            // Save the changes to the SharedPreferences object
//            editor.commit();
//
//            System.out.println("Have counter in preference");
//        }
//        else{
//            // If the key does not exist, get the default value of 0 for the counter
//            System.out.println("No counter in preference");
//
//            // If the key does not exists, create a new object that contains the "counter" key with value 0
//            this.counter = sharedPreferences.getInt("counter", 0);
//
//            // Get a reference to the SharedPreferences.Editor object
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//
//            // Put the value of the counter into the SharedPreferences object
//            editor.putInt("counter", counter);
//
//            // Save the changes to the SharedPreferences object
//            editor.commit();
//        }

        // Initialize the class variables with the values passed to the constructor
        this.id = id;
        this.websiteName = websiteName;
        this.websiteUrl = websiteUrl;
        this.username = username;
        this.password = password;
    }

    //Define empty constructor in order for firebase method toObject() work
    public Account(){

    }

//    public int getCounter(Context context){
//        // Get a reference to the SharedPreferences object
//        SharedPreferences sharedPreferences = context.getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
//        return sharedPreferences.getInt("counter", 0);
//    }
//
//    public void setCounter(int counter){
//        this.counter = counter;
//    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getWebsiteName() {
        return websiteName;
    }

    public void setWebsiteName(String websiteName) {
        this.websiteName = websiteName;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
