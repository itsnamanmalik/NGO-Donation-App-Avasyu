package com.glau.avasyu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddNGOActivity extends AppCompatActivity {

    EditText ngo_name, ngo_location, ngo_date, ngo_desc, ngo_link, contact_details,img_link;
    Button add_btn;
    ProgressBar progressDialog;

    DatabaseReference root= FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addngo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //initialising views
        progressDialog = findViewById(R.id.admin_progress);
        ngo_name = findViewById(R.id.name_et);
        ngo_location = findViewById(R.id.location_et);
        ngo_date = findViewById(R.id.date_et);
        ngo_desc = findViewById(R.id.desc_et);
        ngo_link = findViewById(R.id.link_et);
        contact_details = findViewById(R.id.contact_et);
        add_btn = findViewById(R.id.add_ngo_btn);
        img_link = findViewById(R.id.img_link_et);
        //initialising views
        progressDialog.setVisibility(View.GONE);

        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                progressDialog.setVisibility(View.VISIBLE);


                    if (checkinternet()) {


                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(progressDialog.getVisibility()==View.VISIBLE) {
                                    DatabaseReference ngoref = root.child("ngo");
                                    DatabaseReference ds = ngoref.push();
                                    ds.setValue(new NGOModel(ngo_name.getText().toString(), ngo_location.getText().toString(), img_link.getText().toString(), ngo_desc.getText().toString(), ngo_link.getText().toString(), contact_details.getText().toString(), ngo_date.getText().toString()));
                                    progressDialog.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(),"!!!NGO ADDED!!!",Toast.LENGTH_LONG).show();
                                }
                            }
                    }, 1000);
                }

                else {
                    nointernetconnection();
                    progressDialog.setVisibility(View.GONE);
                }


            }
        });


    }

    //Check Internet
    public boolean checkinternet()
    {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else
            connected = false;
        return connected;
    }
    //CheckInternet


    //No Internet SnackBar Prompt
    private void nointernetconnection() {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar.make(parentLayout, "No Internet Connection!!", Snackbar.LENGTH_INDEFINITE)
                .setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(checkinternet()==true) {
                            finish();
                            startActivity(getIntent());
                        }
                        else
                        {
                            nointernetconnection();
                        }
                    }
                })
                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                .show();
    }
    //No Internet SnackBar Prompt

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
