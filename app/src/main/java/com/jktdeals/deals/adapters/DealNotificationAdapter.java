package com.jktdeals.deals.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
        ImageView ivDealPictureCategory = (ImageView) convertView.findViewById(R.id.ivDealPictureCategory);
        // Populate the data into the template view using the data object
        tvDealTitle.setText(deal.getDealAbstract());
        tvDealDescription.setText(deal.getDealDescription());
        ivDealPictureCategory.setImageResource(R.drawable.restaurant);

//        switch (deal.getCategories().toString()){
//            case "Cafe":
//                ivDealPictureCategory.setImageResource(R.drawable.coffee);
//                break;
//            case "Bar":
//                ivDealPictureCategory.setImageResource(R.drawable.bar);
//                break;
//            case "Restaurant":
//                ivDealPictureCategory.setImageResource(R.drawable.restaurant);
//                break;
//            case "Hotel":
//                ivDealPictureCategory.setImageResource(R.drawable.hotel);
//                break;
//            case "Beauty":
//                ivDealPictureCategory.setImageResource(R.drawable.beauty);
//                break;
//            case "Entertainment":
//                ivDealPictureCategory.setImageResource(R.drawable.entertainment);
//                break;
//            case "Pets":
//                ivDealPictureCategory.setImageResource(R.drawable.pets);
//                break;
//            case "Activities":
//                ivDealPictureCategory.setImageResource(R.drawable.activities);
//                break;
//            case "Massage":
//                ivDealPictureCategory.setImageResource(R.drawable.massage);
//                break;
//            case "Apparel":
//                ivDealPictureCategory.setImageResource(R.drawable.apparel);
//                break;
//            case "Groceries":
//                ivDealPictureCategory.setImageResource(R.drawable.groceries);
//                break;
//            case "Local Services":
//                ivDealPictureCategory.setImageResource(R.drawable.localservice);
//                break;
//            case "Home Services":
//                ivDealPictureCategory.setImageResource(R.drawable.homeservice);
//                break;
//            case "Health":
//                ivDealPictureCategory.setImageResource(R.drawable.health);
//                break;
//        }


        // Return the completed view to render on screen
        return convertView;
    }

    private int getCotegoryIndex(String value){
        switch (value){
            case "Cafe": return 0;
            case "Bar": return 1;
            case "Restaurant": return 2;
            case "Hotel": return 3;
            case "Beauty": return 4;
            case "Entertainment": return 5;
            case "Pets": return 6;
            case "Activities": return 7;
            case "Massage": return 8;
            case "Apparel": return 9;
            case "Groceries": return 10;
            case "Local_Services": return 11;
            case "Home_Services": return 12;
            case "Health": return 13;

        }

        return 0;
    }

    private DealModel.Category getCategory(String value){
        switch (value){
            case "Cafe": return DealModel.Category.Cafe;
            case "Bar": return DealModel.Category.Bar;
            case "Restaurant": return DealModel.Category.Restaurant;
            case "Hotel": return DealModel.Category.Hotel;
            case "Beauty": return DealModel.Category.Beauty;
            case "Entertainment": return DealModel.Category.Entertainment;
            case "Pets": return DealModel.Category.Pets;
            case "Activities": return DealModel.Category.Activities;
            case "Massage": return DealModel.Category.Massage;
            case "Apparel": return DealModel.Category.Apparel;
            case "Groceries": return DealModel.Category.Groceries;
            case "Local Services": return DealModel.Category.Local_Services;
            case "Home Services": return DealModel.Category.Home_Services;
            case "Health": return DealModel.Category.Health;

        }

        return DealModel.Category.Activities;
    }
}
