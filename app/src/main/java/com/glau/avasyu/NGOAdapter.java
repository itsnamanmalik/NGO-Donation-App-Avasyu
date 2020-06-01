package com.glau.avasyu;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class NGOAdapter extends ArrayAdapter {

    Context mContext;
    Activity mActivity;
    private ArrayList<NGOModel> dataSet;
    private ArrayList<String> keys;
    Boolean isadmin;

    private class ViewHolder {
        TextView ngo_name, ngo_location;
        ImageView ngo_img;
        MaterialButton dlt_btn;
    }

    public NGOAdapter(ArrayList data, ArrayList keys, Context context, Activity mActivity, Boolean isadmin) {
        super(context, R.layout.ngo_list_item, data);
        this.dataSet = data;
        this.keys = keys;
        this.isadmin =isadmin;
        this.mContext = context;
        this.mActivity = mActivity;
    }

    @Override
    public int getCount() {
        return dataSet.size();
    }

    @Override
    public NGOModel getItem(int i) {
        return dataSet.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {


        final ViewHolder viewHolder;
        final View result;

        if (view == null) {

            viewHolder = new ViewHolder();
            view=mActivity.getLayoutInflater().inflate(R.layout.ngo_list_item, null);
            viewHolder.ngo_name=view.findViewById(R.id.ngo_list_name);
            viewHolder.ngo_location=view.findViewById(R.id.ngo_list_location);
            viewHolder.ngo_img=view.findViewById(R.id.ngo_list_img);
            viewHolder.dlt_btn=view.findViewById(R.id.delete_btn);

            result=view;
            view.setTag(viewHolder);

        }
        else {
            viewHolder = (ViewHolder) view.getTag();
            result=view;
        }

        NGOModel item = getItem(i);

        if(isadmin){
            viewHolder.dlt_btn.setVisibility(View.VISIBLE);
            viewHolder.dlt_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    androidx.appcompat.app.AlertDialog.Builder builder1 = new androidx.appcompat.app.AlertDialog.Builder(mContext);
                    builder1.setMessage("Are You sure you want to DELETE NGO");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    int var=i;
                                    String key=keys.get(var).toString();
                                    DatabaseReference ngo_details = FirebaseDatabase.getInstance().getReference().child("ngo");
                                    ngo_details.child(key).removeValue();
                                    Toast.makeText(mContext,"NGO DELETED",Toast.LENGTH_LONG).show();
                                }
                            });

                    builder1.setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }
            });
        }

        viewHolder.ngo_name.setText(item.getName());
        viewHolder.ngo_location.setText(item.getLocation());
        Picasso.get()
                .load(item.getImg_link())
                .fit()
                .centerInside()
                .into(viewHolder.ngo_img);
        return result;

    }
}