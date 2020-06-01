package com.glau.avasyu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

public class NgoDetailsActivity extends AppCompatActivity {

    TextView ngo_name, ngo_location, ngo_date, ngo_desc, ngo_link, contact_details;
    ImageView ngo_img;
    Button donate_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ngo_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //initialising views
        ngo_name = findViewById(R.id.ngo_name);
        ngo_location = findViewById(R.id.ngo_location);
        ngo_date = findViewById(R.id.ngo_date);
        ngo_desc = findViewById(R.id.ngo_desc);
        ngo_link = findViewById(R.id.ngo_link);
        contact_details = findViewById(R.id.contact_details);
        ngo_img = findViewById(R.id.ngo_img);
        donate_btn = findViewById(R.id.donate_btn);
        //initialising views


        ngo_name.setText(getIntent().getExtras().getString("name"));
        ngo_location.setText(getIntent().getExtras().getString("location"));
        ngo_date.setText(getIntent().getExtras().getString("date"));
        ngo_desc.setText(getIntent().getExtras().getString("desc"));
        ngo_link.setText(getIntent().getExtras().getString("link"));
        contact_details.setText(getIntent().getExtras().getString("contact"));

        Picasso.get()
                .load(getIntent().getExtras().getString("imglink"))
                .centerInside()
                .fit()
                .into(ngo_img);

        ngo_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url=ngo_link.getText().toString();
                if (!url.startsWith("http://") && !url.startsWith("https://"))
                    url = "http://" + url;
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        });

        donate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),DonationActivity.class);
                intent.putExtra("pay_ngo",ngo_name.getText().toString());
                startActivity(intent);
            }
        });


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
