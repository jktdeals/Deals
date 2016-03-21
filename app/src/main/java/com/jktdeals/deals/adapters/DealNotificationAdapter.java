package com.jktdeals.deals.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jktdeals.deals.R;
import com.jktdeals.deals.models.DealModel;

import java.util.List;

/**
 * Created by josevillanuva on 3/20/16.
 */
public class DealNotificationAdapter extends ArrayAdapter {

    private int resource;
    private LayoutInflater inflater;
    private Context context;

    public DealNotificationAdapter ( Context ctx, int resourceId, List apps) {

        super( ctx, resourceId, apps );
        resource = resourceId;
        inflater = LayoutInflater.from( ctx );
        context=ctx;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position

        convertView = (LinearLayout) inflater.inflate(resource, null);

        DealModel deal = (DealModel) getItem(position);

        TextView tvDealTitle = (TextView) convertView.findViewById(R.id.tvDealTitle);
        TextView tvDealDescription = (TextView) convertView.findViewById(R.id.tvDealDescription);
        // Populate the data into the template view using the data object
        tvDealTitle.setText(deal.getDealAbstract());
        tvDealDescription.setText(deal.getDealDescription());
        // Return the completed view to render on screen
        return convertView;
    }
}
