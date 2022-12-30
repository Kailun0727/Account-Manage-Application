package com.example.accountmanage;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.accountmanage.databinding.FragmentAccountsBinding;
import com.example.accountmanage.databinding.ListItemAccountBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.app.Activity.RESULT_OK;

public class AccountListFragment extends Fragment{

    private FirebaseFirestore firestore;

    private AccountAdapter mAdapter;

    private FragmentAccountsBinding mAccountsBinding;

    private List<Account> mAccountList;

    private FloatingActionButton fab;

    private boolean mFirstLaunch = true;

    private static final int REQUEST_ADD_ACCOUNT = 1;

    private  ActivityResultLauncher<Intent> mLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        //if the result has been call back
                        if (result.getResultCode() == RESULT_OK){
                            //if the result is ok, then update the data set of adapter to show latest data
                            mAdapter.updateDataSet();
                        }
                    }
                }

        );

    }

    private void filter(String text) {
        // creating a new array list to filter our data.
        List<Account> filteredlist = new ArrayList<>();

        // running a for loop to compare elements.
        for (Account item : mAccountList) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.getWebsiteName().toLowerCase().contains(text.toLowerCase())
                ||item.getWebsiteUrl().toLowerCase().contains(text.toLowerCase())
                || item.getUsername().toLowerCase().contains(text.toLowerCase())
                || item.getPassword().toLowerCase().contains(text.toLowerCase())
            ) {
                // if the item is matched we are then adding it to our filtered list.
                filteredlist.add(item);
            }
        }

        if (filteredlist.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
            Toast.makeText(getActivity(), "No Data Found..", Toast.LENGTH_SHORT).show();
        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            mAdapter.setFilter(filteredlist);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //need to setHasOptionsMenu to true if we want to have options menu in Fragment
        setHasOptionsMenu(true);

        //bind layout
        mAccountsBinding = FragmentAccountsBinding.inflate(getLayoutInflater());

        fab = mAccountsBinding.FABAdd;



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(),AddAccount.class);

                //use result launcher to launch intent
                mLauncher.launch(i);
            }
        });

        return mAccountsBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Create a new LinearLayoutManager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        // Set the orientation of the LinearLayoutManager to show items from top to bottom
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mAccountsBinding.recyclerView.setLayoutManager(layoutManager);

        updateUI();

    }

    // calling on create option menu
    // layout to inflate our menu file.
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu,inflater);

        // inside inflater we are inflating our menu file.
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.actionSearch);

        // getting search view of our item.
        SearchView searchView = (SearchView)searchItem.getActionView();

        Log.d("Menu created","Menu Working");

        //implement listener of search view
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //use filter function to search data
                filter(newText);
                return false;
            }
        });

    }


    //method for update interface
    private void updateUI() {
        firestore = FirebaseFirestore.getInstance();

        mAccountList = new ArrayList<>();

        //Retrieve all documents from the Account collection
        firestore.collection("accounts").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    //Code will not work if we put the code after onSuccess or OnFailure method, mean we need to handle all process in either onSuccess or OnFailure
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {

                        // Check if the collection is empty
                        if (querySnapshot.isEmpty()) {
                            Toast.makeText(getContext(),"No Data, Please add some data!",Toast.LENGTH_SHORT).show();
                            System.out.println("The collection is empty");
                        } else {
                            // Loop through the documents and add them to the ArrayList
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                // Convert the document into an Account object
                                Account account = document.toObject(Account.class);

                                // Add the Account object to the ArrayList
                                mAccountList.add(account);

                                // Check if the adapter is null
                                if (mAdapter == null){
                                    // Create a new adapter with the account list
                                    mAdapter = new AccountAdapter(mAccountList);

                                    // Set the adapter for the RecyclerView
                                    mAccountsBinding.recyclerView.setAdapter(mAdapter);

                                    // Handle card view swipe functionality
                                    ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                                            // Enable swipe in all directions
                                            ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT| ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                                            // Enable swipe in left and right directions
                                            ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                                        @Override
                                        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                                            // Return false to disable drag and drop
                                            return false;
                                        }

                                        @Override
                                        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                                            // Remove the item from the data set and notify the adapter
                                            int position = viewHolder.getBindingAdapterPosition();

                                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                            builder.setTitle("Confirm delete");
                                            builder.setMessage("Are you sure you want to delete this account?");

                                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // Perform delete action here
                                                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                                                    //need to get the id from account list before the account deleted
                                                    String id = mAccountList.get(position).getId();

                                                    DocumentReference docRef = firestore.collection("accounts").document(id);

                                                    //delete the document
                                                    docRef.delete();

                                                    Toast.makeText(getActivity(), "Account deleted!", Toast.LENGTH_SHORT).show();

                                                    System.out.println("Account : "+ id +" has been deleted");

                                                    //delete the account in account list
                                                    mAccountList.remove(position);

                                                    mAdapter.notifyItemRemoved(position);

                                                }
                                            });
                                            //This will prevent the item from being removed from the adapter when the user clicks "No" on the alert dialog.
                                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // Notify the adapter that the item has changed
                                                    mAdapter.notifyItemChanged(position);
                                                    // Dismiss the dialog
                                                    dialog.dismiss();
                                                }
                                            });

                                            //Create and show the dialog:
                                            AlertDialog dialog = builder.create();
                                            dialog.show();
                                        }
                                    });



                                    // Attach the ItemTouchHelper to the RecyclerView
                                    helper.attachToRecyclerView(mAccountsBinding.recyclerView);

                                } else{
                                    mAdapter.notifyDataSetChanged();
                                }

                                // Check if the adapter has any data to display
                                if (mAdapter.getItemCount() == 0) {
                                    // The adapter is empty, so there is no data to display
                                    System.out.println("Adapter is empty");
                                    // You can add data to the adapter here
                                } else {
                                    // The adapter has data, so the RecyclerView should show something
                                    // If the RecyclerView is not showing anything, there may be another issue
                                    System.out.println("Adapter is not empty");
                                }
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(),"Failure",Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Adapter of account
    public class AccountAdapter extends RecyclerView.Adapter<AccountHolder>{

        // List of accounts to be displayed
        private List<Account> mAccounts;

        public AccountAdapter(List<Account> accounts){

            // Store the list of accounts
            mAccounts = accounts;
        }

        @NonNull
        @Override
        public AccountHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // Inflate the layout for the account item
            ListItemAccountBinding accountBinding = ListItemAccountBinding.inflate(getLayoutInflater());

            // Return a new view holder with the account binding
            return new AccountHolder(accountBinding);
        }

        @Override
        public void onBindViewHolder(@NonNull AccountHolder holder, int position) {


            // Get the account at the given position
            Account account = mAccounts.get(position);

            // Bind the account to the view holder
            holder.bindAccount(account);
        }

        @Override
        public int getItemCount() {
            // Return the number of accounts
            return mAccounts.size();
        }


        // Method for filtering the accounts in the RecyclerView
        public void setFilter(List<Account> accountList) {
            // Clear the current list of accounts
            mAccounts = new ArrayList<>();

            // Add the filtered accounts to the list
            mAccounts.addAll(accountList);

            // Notify the adapter of the change in the data set
            notifyDataSetChanged();
        }

        //method for update the dataset of array list
        public void updateDataSet(){
            firestore = FirebaseFirestore.getInstance();

            //clear the previous data
            mAccounts.clear();

            mAccounts = new ArrayList<>();

            //Retrieve all documents from the Account collection
            firestore.collection("accounts").get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        //Code will not work if we put the code after onSuccess or OnFailure method, mean we need to handle all process in either onSuccess or OnFailure
                        @Override
                        public void onSuccess(QuerySnapshot querySnapshot) {

                            // Check if the collection is empty
                            if (querySnapshot.isEmpty()) {
                                Toast.makeText(getContext(),"No Data, Please add some data!",Toast.LENGTH_SHORT).show();
                                System.out.println("The collection is empty");
                            } else {
                                // Loop through the documents and add them to the ArrayList
                                for (QueryDocumentSnapshot document : querySnapshot) {
                                    // Convert the document into an Account object
                                    Account account = document.toObject(Account.class);

                                    // Add the Account object to the ArrayList
                                    mAccounts.add(account);

                                }
                                //notify the adapter after all account has been added
                                notifyDataSetChanged();

                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(),"Failure",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    //Holder of account
    public class AccountHolder extends RecyclerView.ViewHolder{

        // Add a field to store the document ID
        private String documentId;

        private Account mAccount;
        private TextView mId;
        private TextView mWebsiteName;
        private TextView mWebsiteUrl;
        private TextView mUsername;
        private TextView mPassword;
        private Button mEditButton;

        public AccountHolder(ListItemAccountBinding binding) {
            super(binding.getRoot());

            //bind the text view
            mId = binding.id;
            mWebsiteName = binding.websiteName;
            mWebsiteUrl = binding.websiteUrl;
            mUsername = binding.username;
            mPassword = binding.password;
            mEditButton = binding.editButton;

        }

        public void bindAccount(Account account){
            mAccount = account;


            mId.setText(account.getId());
            mWebsiteName.setText(account.getWebsiteName());

            System.out.println("Counter :"+account.getUsername());

            mWebsiteUrl.setText(account.getWebsiteUrl());

            //Open url by using browser intent
            mWebsiteUrl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String url = "https://"+mWebsiteUrl.getText().toString();
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
            });

            mUsername.setText("Username : "+account.getUsername());

            //Copy function for username
            mUsername.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // Get the text from the TextView
                    String text = mUsername.getText().toString();

                    // Find the index of the colon in the text
                    int colonIndex = text.indexOf(":");

                    // Check if the colon was found
                    if (colonIndex >= 0) {
                        // Extract the part of the string after the colon
                        String part = text.substring(colonIndex + 2);
                        // part will contain the username

                        // Get the clipboard manager and create a new clip data object
                        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("Username", part);

                        // Set the clip data to the clipboard
                        clipboard.setPrimaryClip(clip);

                        // Show a toast message
                        Toast.makeText(getActivity(), "Username copied to clipboard", Toast.LENGTH_SHORT).show();
                    }

                }
            });

            mPassword.setText("Password : "+account.getPassword());

            //Copy function for password
            mPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Get the text from the TextView
                    String text = mPassword.getText().toString();

                    // Find the index of the colon in the text
                    int colonIndex = text.indexOf(":");

                    // Check if the colon was found
                    if (colonIndex >= 0) {
                        // Extract the part of the string after the colon
                        String part = text.substring(colonIndex + 2);
                        // part will contain the username

                        // Get the clipboard manager and create a new clip data object
                        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("Password", part);

                        // Set the clip data to the clipboard
                        clipboard.setPrimaryClip(clip);

                        // Show a toast message
                        Toast.makeText(getActivity(), "Password copied to clipboard", Toast.LENGTH_SHORT).show();

                    }
                }
            });

            mEditButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent i = new Intent(getActivity(),UpdateAccount.class);
                    i.putExtra("id",mId.getText().toString());
                    mLauncher.launch(i);
                }
            });

        }


    }
}
