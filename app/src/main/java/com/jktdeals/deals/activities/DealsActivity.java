package com.jktdeals.deals.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

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

        String dealValue = "50%";
        String dealAbstract = "All ice cream pints half price";
        String dealDescription = "April Fools Day is half-price day for all one-pint containers of ice cream";
        String dealRestrictions = "April 1, 2016 only, pint containers only";
        String dealExpiry = "April 1, 2016";
        String dealPic = "";
        LatLng latLang = new LatLng(gpsHelper.getLatitude(), gpsHelper.getLongitude());
        String storeName = "New Leaf Market";
        String storeAbstract = "";
        String storeDescription = "";
        String storeLogo = "";
        String storePic = "";

        // createDeal
        DealModel dealObj = pi.createDealObject(dealValue, dealAbstract, dealDescription,
                dealRestrictions, dealExpiry, dealPic,

                new LatLng(gpsHelper.getLatitude(), gpsHelper.getLongitude()),

                storeName, storeAbstract, storeDescription,
                storeLogo, storePic
        );
        //pi.publishDeal(dealObj);
//
//        final ParseInterface.dealLoadNotifier nfy = new ParseInterface.dealLoadNotifier() {
//            @Override
//            public void notifyLoad(int noOfItems) {
//                texty.setText("total deals: " + noOfItems + " first: " + dealModelArrayList.get(0).getDealValue());
//            }
//        };
//
//        // getDeals
//        pi.getDeals(dealModelArrayList, nfy);



    }

}
