package com.jktdeals.deals.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.jktdeals.deals.R;
import com.jktdeals.deals.adapters.DealsFragmentPagerAdapter;
import com.jktdeals.deals.helpers.GPSHelper;
import com.jktdeals.deals.models.DealModel;
import com.jktdeals.deals.parse.ParseInterface;

import java.util.ArrayList;

public class DealsActivity extends AppCompatActivity {

    private ParseInterface pi;
    private ArrayList<DealModel> dealModelArrayList;
    private GPSHelper gpsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deals);

        // get rid of actionbar drop shadow
        getSupportActionBar().setElevation(0);

        // Get the ViewPager and set its PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.vpPager);
        viewPager.setAdapter(new DealsFragmentPagerAdapter(getSupportFragmentManager(),
                DealsActivity.this));

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        // Initialize Parse
        pi = ParseInterface.getInstance(this);

        // Initialize GPS Helper
        gpsHelper = new GPSHelper(this);

        // Allocate Deal List
        dealModelArrayList = new ArrayList<>();

        //TODO : this is to demonstrate how to use the getDeals and createDeal functions, remove this once seen.
        final TextView texty = (TextView) findViewById(R.id.texty);

        // createDeal
        DealModel dealObj = pi.createDealObject("$5", "on burrito", "$5 off on a burrito when you buy another at full price",
                "one per person, cannot be combines with other offers,", "in 2 days!", "dealPicUrl",

                new LatLng(gpsHelper.getLatitude(), gpsHelper.getLongitude()),

                "Curry Up Now", "Indian food with American Twist", "Curry Up Now has multiple branches in bay area and food trucks",
                "logoUrl", "storePicUrl"
        );
        pi.publishDeal(dealObj);

        final ParseInterface.dealLoadNotifier nfy = new ParseInterface.dealLoadNotifier() {
            @Override
            public void notifyLoad(int noOfItems) {
                texty.setText("total deals: " + noOfItems + " first: " + dealModelArrayList.get(0).getDealValue());
            }
        };

        // getDeals
        pi.getDeals(dealModelArrayList, nfy);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_items, menu);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_create_deal:
                Intent intent = new Intent(DealsActivity.this, CreatDealActivity.class);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
