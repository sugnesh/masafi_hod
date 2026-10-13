package com.engear.vansales.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.engear.vansales.R;

import java.util.ArrayList;


/**
 * Created by Rakshit on 20-Jan-17.
 */
public class CustomerStatusAdapter extends ArrayAdapter<String> {

    private ArrayList<String> statuses;

    public CustomerStatusAdapter(Context context, ArrayList<String> customerStatuses) {
        super(context, R.layout.badge_customer_status, customerStatuses);
        this.statuses = customerStatuses;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.badge_customer_status, parent, false);
            // get all UI view
            holder = new ViewHolder(convertView);
            // set tag for holder
            convertView.setTag(holder);
        } else {
            // if holder created, get tag from view
            holder = (ViewHolder) convertView.getTag();
        }
        Log.e("Status", "" + statuses.get(position));
        holder.item_description.setText(statuses.get(position));
        return convertView;
    }

    private class ViewHolder {
        private TextView item_code;
        private TextView item_description;

        public ViewHolder(View v) {
            item_description = (TextView) v.findViewById(R.id.tv_shop_status);
        }
    }
}
