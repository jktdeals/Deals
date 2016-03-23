package com.jktdeals.deals.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jktdeals.deals.R;

/**
 * Created by josevillanuva on 3/22/16.
 */
public class CategoryAdapter extends ArrayAdapter<String> {

    LayoutInflater inflater;
    public CategoryAdapter(Context context, int textViewResourceId,   String[] objects) {
        super(context, textViewResourceId, objects);

        inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
    }

    @Override
    public View getDropDownView(int position, View convertView,ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {

        String selectedCatherfory = getItem(position);

        View row = inflater.inflate(R.layout.item_category, parent, false);
        TextView tvCategory = (TextView)row.findViewById(R.id.tvCategory);
        tvCategory.setText(selectedCatherfory);

        ImageView ivCategoryIcon = (ImageView)row.findViewById(R.id.ivICategoryIcon);

        switch (selectedCatherfory){
            case "Cafe":
                ivCategoryIcon.setImageResource(R.drawable.coffee);
                break;
            case "Bar":
                ivCategoryIcon.setImageResource(R.drawable.bar);
                break;
            case "Restaurant":
                ivCategoryIcon.setImageResource(R.drawable.restaurant);
                break;
            case "Hotel":
                ivCategoryIcon.setImageResource(R.drawable.hotel);
                break;
            case "Beauty":
                ivCategoryIcon.setImageResource(R.drawable.beauty);
                break;
            case "Entertainment":
                ivCategoryIcon.setImageResource(R.drawable.entertainment);
                break;
            case "Pets":
                ivCategoryIcon.setImageResource(R.drawable.pets);
                break;
            case "Activities":
                ivCategoryIcon.setImageResource(R.drawable.activities);
                break;
            case "Massage":
                ivCategoryIcon.setImageResource(R.drawable.massage);
                break;
            case "Apparel":
                ivCategoryIcon.setImageResource(R.drawable.apparel);
                break;
            case "Groceries":
                ivCategoryIcon.setImageResource(R.drawable.groceries);
                break;
            case "Local Services":
                ivCategoryIcon.setImageResource(R.drawable.localservice);
                break;
            case "Home Services":
                ivCategoryIcon.setImageResource(R.drawable.homeservice);
                break;
            case "Health":
                ivCategoryIcon.setImageResource(R.drawable.health);
                break;
        }

        //ivCategoryIcon.setImageResource(R.drawable.groceries);

        return row;
    }
}
