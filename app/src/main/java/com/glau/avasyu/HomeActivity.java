package com.glau.avasyu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ArrayList keys = new ArrayList<String>();
    ArrayList NGODataArrayList=new ArrayList<NGOModel>();
    NGOAdapter ngo_adapter;

    ListView ngo_list;
    TextView user_name,user_email;
    ImageView user_image;
    DrawerLayout drawer;
    LottieAnimationView verified;
    Boolean check_admin = false;

    DatabaseReference root= FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //toolbar
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //toolbar


        //navigation drawer
        drawer=findViewById(R.id.drawer_layout);
        NavigationView navigationView=findViewById(R.id.nac_view);
        Menu nav_Menu = navigationView.getMenu();
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        //navigation drawer

        //initialize
        ngo_list=findViewById(R.id.ngo_list);
        user_name=navigationView.getHeaderView(0).findViewById(R.id.name_txt);
        user_email=navigationView.getHeaderView(0).findViewById(R.id.email_txt);
        user_image=navigationView.getHeaderView(0).findViewById(R.id.user_img);
        verified = navigationView.getHeaderView(0).findViewById(R.id.verified);
        //initialize



        if(isadmin()){
            nav_Menu.findItem(R.id.add_ngo).setVisible(true);
            nav_Menu.findItem(R.id.delete_ngo).setVisible(true);
            verified.setVisibility(View.VISIBLE);
            androidx.appcompat.app.AlertDialog.Builder builder1 = new androidx.appcompat.app.AlertDialog.Builder(HomeActivity.this);
            builder1.setMessage("Congratulations You Have Admin Rights");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert11 = builder1.create();
            alert11.show();

        }


        //get user data
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user_name.setText(user.getDisplayName());
            user_email.setText(user.getEmail());
            Picasso.get()
                    .load(user.getPhotoUrl())
                    .centerInside()
                    .fit()
                    .placeholder(R.drawable.ic_account)
                    .into(user_image);
        }
        //get user data

        //get data from database
        getdata();
        //get data from database

        //List Setup
        ngo_adapter=new NGOAdapter(NGODataArrayList,keys,getApplicationContext(),HomeActivity.this,check_admin);
        ngo_list.setAdapter(ngo_adapter);
        //List Setup


        //List click listener
        ngo_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(getApplicationContext(),NgoDetailsActivity.class);
                intent.putExtra("name", ngo_adapter.getItem(i).getName());
                intent.putExtra("location", ngo_adapter.getItem(i).getLocation());
                intent.putExtra("date", ngo_adapter.getItem(i).getEst_date());
                intent.putExtra("desc", ngo_adapter.getItem(i).getDescription());
                intent.putExtra("link", ngo_adapter.getItem(i).getOfficial_site());
                intent.putExtra("contact", ngo_adapter.getItem(i).getContact());
                intent.putExtra("imglink", ngo_adapter.getItem(i).getImg_link());

                startActivity(intent);
            }
        });
        //List click listener

    }

    private boolean isadmin() {
        boolean admin;
        admin = getIntent().getExtras().getBoolean("isadmin",false);
        return (admin);
    }


    //Getting Data in Realtime
    private void getdata() {

        DatabaseReference ngo_details = root.child("ngo");
        ngo_details.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                NGOModel NGOData=dataSnapshot.getValue(NGOModel.class);

                assert NGOData != null;
                NGODataArrayList.add(new NGOModel(NGOData.getName(), NGOData.getLocation(), NGOData.getImg_link(), NGOData.getDescription(), NGOData.getOfficial_site(), NGOData.getContact(), NGOData.getEst_date()));
                keys.add(dataSnapshot.getKey());
                ngo_adapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                int index = keys.indexOf(dataSnapshot.getKey());
                NGODataArrayList.remove(index);
                ngo_adapter.notifyDataSetChanged();
                keys.remove(dataSnapshot.getKey());

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    //Getting Data in Realtime

    //Check Internet
    public boolean checkinternet()
    {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        //we are connected to a network
        connected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
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


    //Navigation Drawer Listener
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.nav_home:
                check_admin=false;
                finish();
                startActivity(getIntent());
                break;
            case R.id.nav_share:
                final String appPackageName = getPackageName();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey, Check out awesome Deals at: https://play.google.com/store/apps/details?id="+appPackageName+" and check out more awesome apps at:https://play.google.com/store/apps/dev?id=9031308046643550638");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;
            case R.id.nav_logout:
                AuthUI.getInstance()
                        .signOut(getApplicationContext())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Intent intent=new Intent(getApplicationContext(), SignIn.class);
                                startActivity(intent);
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),""+e.getMessage(),Toast.LENGTH_LONG).show();

                    }
                });
                break;
            case R.id.nav_payments:
                startActivity(new Intent(getApplicationContext(), PaymentsActivity.class));
                break;
            case R.id.add_ngo:
                startActivity(new Intent(getApplicationContext(), AddNGOActivity.class));
                break;
            case R.id.delete_ngo:
                ngo_adapter=new NGOAdapter(NGODataArrayList,keys,HomeActivity.this,HomeActivity.this,isadmin());
                ngo_list.setAdapter(ngo_adapter);

        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    //Navigation Drawer Listener


    //Back Button Nav Drawer Off
    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }
    //Back Button Nav Drawer Off
}
