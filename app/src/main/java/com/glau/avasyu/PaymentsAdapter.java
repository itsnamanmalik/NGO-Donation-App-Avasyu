package com.glau.avasyu;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class PaymentsAdapter extends ArrayAdapter {

    Context mContext;
    Activity mActivity;
    private ArrayList<PaymentModel> dataSet;
//    private ArrayList<DatabaseReference> databaseReferenceArrayList;

    private class ViewHolder {
        TextView payed_ngo_name, payed_amount, payed_ref_no;
    }

    public PaymentsAdapter(ArrayList data, Context context, Activity mActivity) {
        super(context, R.layout.payment_list_item, data);
        this.dataSet = data;
        this.mContext = context;
        this.mActivity = mActivity;
//        this.databaseReferenceArrayList = databaseList;
    }

    @Override
    public int getCount() {
        return dataSet.size();
    }

    @Override
    public PaymentModel getItem(int i) {
        return dataSet.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {


        final PaymentsAdapter.ViewHolder viewHolder;
        final View result;

        if (view == null) {
            viewHolder = new PaymentsAdapter.ViewHolder();

            view=mActivity.getLayoutInflater().inflate(R.layout.payment_list_item, null);
            viewHolder.payed_ngo_name=view.findViewById(R.id.payed_ngo_name);
            viewHolder.payed_amount=view.findViewById(R.id.payed_rupees);
            viewHolder.payed_ref_no=view.findViewById(R.id.payed_ref_no);


            result=view;
            view.setTag(viewHolder);

        } else {
            viewHolder = (PaymentsAdapter.ViewHolder) view.getTag();
            result=view;
        }


        PaymentModel item = getItem(i);

        viewHolder.payed_ngo_name.setText(item.getNgo_name());
        viewHolder.payed_amount.setText("₹"+item.getAmount());
        viewHolder.payed_ref_no.setText("Ref No: "+item.getRefid());
        return result;
    }



}