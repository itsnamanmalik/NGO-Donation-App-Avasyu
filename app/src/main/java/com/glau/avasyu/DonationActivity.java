package com.glau.avasyu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class DonationActivity extends AppCompatActivity {

    Button pay_btn;
    EditText amount;
    TextView ngo_name;
    final int GOOGLE_PAY_REQUEST_CODE = 123;
    String merid;
    final String mername="Naman Malik";
    String name,final_amount;
    TextView refno, success_msg;
    LottieAnimationView done;
    CardView paymentcard;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        name=getIntent().getExtras().getString("pay_ngo");
        merid=getIntent().getExtras().getString("upi");

        pay_btn = findViewById(R.id.pay_btn);
        amount = findViewById(R.id.amount);
        ngo_name = findViewById(R.id.payment_ngo_name);
        refno = findViewById(R.id.refno);
        success_msg = findViewById(R.id.success_message);
        done = findViewById(R.id.success);
        paymentcard = findViewById(R.id.payment_card);


        ngo_name.setText(name);

        pay_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pay_btn.getText().toString().equals("DONATE"))
                {
                    initiate_payment();
                }
                else
                {
                    paymentcard.setVisibility(View.VISIBLE);
                    done.setVisibility(View.GONE);
                    refno.setVisibility(View.GONE);
                    pay_btn.setText("DONATE");
                    success_msg.setVisibility(View.GONE);
                }

            }
        });

    }

    private void initiate_payment() {
        final_amount = amount.getText().toString();
        if (TextUtils.isEmpty(amount.getText().toString().trim())){
            Toast.makeText(getApplicationContext()," Amount is invalid", Toast.LENGTH_SHORT).show();}
        else {
            Uri uri = new Uri.Builder()
                    .scheme("upi")
                    .authority("pay")
                    .appendQueryParameter("pa", merid)
                    .appendQueryParameter("pn", mername)
                    .appendQueryParameter("tn", name)
                    .appendQueryParameter("am", final_amount)
                    .appendQueryParameter("cu", "INR")
                    .build();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            Intent chooser = Intent.createChooser(intent, "Pay with");
            if (null != chooser.resolveActivity(getPackageManager())) {
                DonationActivity.this.startActivityForResult(chooser, GOOGLE_PAY_REQUEST_CODE);
            } else {
                Toast.makeText(DonationActivity.this, "No UPI app found, please install one to continue", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("main ", "response "+resultCode );
        /*
       E/main: response -1
       E/UPI: onActivityResult: txnId=AXI4a3428ee58654a938811812c72c0df45&responseCode=00&Status=SUCCESS&txnRef=922118921612
       E/UPIPAY: upiPaymentDataOperation: txnId=AXI4a3428ee58654a938811812c72c0df45&responseCode=00&Status=SUCCESS&txnRef=922118921612
       E/UPI: payment successfull: 922118921612
         */
        switch (requestCode) {
            case GOOGLE_PAY_REQUEST_CODE:
                if ((RESULT_OK == resultCode) || (resultCode == 11)) {
                    if (data != null) {
                        String trxt = data.getStringExtra("response");
                        Log.e("UPI", "onActivityResult: " + trxt);
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add(trxt);
                        upiPaymentDataOperation(dataList);
                    } else {
                        Log.e("UPI", "onActivityResult: " + "Return data is null");
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add("nothing");
                        upiPaymentDataOperation(dataList);
                    }
                } else {
                    //when user simply back without payment
                    Log.e("UPI", "onActivityResult: " + "Return data is null");
                    ArrayList<String> dataList = new ArrayList<>();
                    dataList.add("nothing");
                    upiPaymentDataOperation(dataList);
                }
                break;
        }
    }
    private void upiPaymentDataOperation(ArrayList<String> data) {
        if (isConnectionAvailable(DonationActivity.this)) {
            String str = data.get(0);
            Log.e("UPIPAY", "upiPaymentDataOperation: "+str);
            String paymentCancel = "";
            if(str == null) str = "discard";
            String status = "";
            String approvalRefNo = "";
            String response[] = str.split("&");
            for (int i = 0; i < response.length; i++) {
                String equalStr[] = response[i].split("=");
                if(equalStr.length >= 2) {
                    if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) {
                        status = equalStr[1].toLowerCase();
                    }
                    else if (equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase()) || equalStr[0].toLowerCase().equals("txnRef".toLowerCase())) {
                        approvalRefNo = equalStr[1];
                    }
                }
                else {
                    paymentCancel = "Payment cancelled by user.";
                }
            }
            if (status.equals("success")) {

                //Code to handle successful transaction here.
                DatabaseReference user = FirebaseDatabase.getInstance().getReference().child("users");
                user.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("payments")
                        .push()
                        .setValue(new PaymentModel(name,final_amount,approvalRefNo));

                paymentcard.setVisibility(View.GONE);
                done.setVisibility(View.VISIBLE);
                refno.setText("Ref No: "+approvalRefNo);
                refno.setVisibility(View.VISIBLE);
                pay_btn.setText("DONATE AGAIN");
                success_msg.setText("₹"+final_amount+" DONATED SUCCESSFULLY");
                success_msg.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Transaction successful."+approvalRefNo, Toast.LENGTH_LONG).show();
                Log.e("UPI", "payment successfull: "+approvalRefNo);

            }
            else if("Payment cancelled by user.".equals(paymentCancel)) {
                Toast.makeText(getApplicationContext(), "Payment cancelled by user.", Toast.LENGTH_SHORT).show();
                Log.e("UPI", "Cancelled by user: "+approvalRefNo);
            }
            else {
                Toast.makeText(getApplicationContext(), "Transaction failed.Please try again", Toast.LENGTH_SHORT).show();
                Log.e("UPI", "failed payment: "+approvalRefNo);
            }
        } else {
            Log.e("UPI", "Internet issue: ");
            Toast.makeText(getApplicationContext(), "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean isConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable()) {
                return true;
            }
        }
        return false;
    }

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
