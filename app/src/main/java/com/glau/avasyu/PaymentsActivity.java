package com.glau.avasyu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PaymentsActivity extends AppCompatActivity {

    ListView payments_list;
    PaymentsAdapter payments_adapter;
    ArrayList keys = new ArrayList<String>();
    TextView nothing;
    ArrayList paymentsDataArrayList =new ArrayList<PaymentModel>();
    DatabaseReference user = FirebaseDatabase.getInstance().getReference().child("users");
    DatabaseReference payements= user.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
            .child("payments");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        payments_list = findViewById(R.id.payments_list);
        nothing = findViewById(R.id.nothing);

        getdata();

        //List Setup
        payments_adapter = new PaymentsAdapter(paymentsDataArrayList, getApplicationContext(), PaymentsActivity.this);
        payments_list.setAdapter(payments_adapter);
        //List Setup




        payments_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                DatabaseReference user = FirebaseDatabase.getInstance().getReference().child("users");
                DatabaseReference payements= user.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("payments").child(keys.get(i).toString());

                payements.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        Toast.makeText(getApplicationContext(),dataSnapshot.child("amount").getValue().toString(),Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

//                Uri uri = Uri.parse(); // missing 'http://' will cause crashed
//                startActivity(new Intent(Intent.ACTION_VIEW, uri));

            }
        });
//
    }


    //Getting Data in Realtime
    private void getdata() {


        payements.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                PaymentModel paymentsData=dataSnapshot.getValue(PaymentModel.class);

                assert paymentsData != null;
                paymentsDataArrayList.add(new PaymentModel(paymentsData.getNgo_name(), paymentsData.getAmount(), paymentsData.getRefid()));
                keys.add(dataSnapshot.getKey());
                payments_adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                int index = keys.indexOf(dataSnapshot.getKey());
                paymentsDataArrayList.remove(index);
                payments_adapter.notifyDataSetChanged();
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
