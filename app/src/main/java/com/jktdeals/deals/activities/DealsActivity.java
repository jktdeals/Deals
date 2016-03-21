package com.jktdeals.deals.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.jktdeals.deals.R;
import com.jktdeals.deals.adapters.DealsFragmentPagerAdapter;
import com.jktdeals.deals.fragments.MyDealsFragment;
import com.jktdeals.deals.helpers.GPSHelper;
import com.jktdeals.deals.models.DealModel;
import com.jktdeals.deals.parse.ParseInterface;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import java.util.ArrayList;

public class DealsActivity extends AppCompatActivity {
    private final int REQUEST_CODE_CREATE_DEAL = 20;
    private DrawerLayout mDrawer;
    private NavigationView nvDrawer;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;
    private ParseInterface pi;
    private ArrayList<DealModel> dealModelArrayList;
    private GPSHelper gpsHelper;
    public ViewPager viewPager;
    public DealsFragmentPagerAdapter dealsFragmentPagerAdapter;


    // ActivityOne.java, time to handle the result of the sub-activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deals);

        // Set a Toolbar to replace the ActionBar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // get rid of actionbar drop shadow
        getSupportActionBar().setElevation(0);

        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();
        mDrawer.addDrawerListener(drawerToggle);
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        // Setup drawer view
        setupDrawerContent(nvDrawer);

        // Get the ViewPager and set its PagerAdapter so that it can display items
        viewPager = (ViewPager) findViewById(R.id.vpPager);
        dealsFragmentPagerAdapter = new DealsFragmentPagerAdapter(getSupportFragmentManager(),
                DealsActivity.this);
        viewPager.setAdapter(dealsFragmentPagerAdapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        // Initialize Parse
        pi = ParseInterface.getInstance(this);

        // Initialize GPS Helper
        gpsHelper = new GPSHelper(this);

        // Allocate Deal List
        dealModelArrayList = new ArrayList<>();
    }

    // `onPostCreate` called when activity start-up is complete after `onStart()`
    // NOTE! Make sure to override the method with only a single `Bundle` argument
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    private void setHeaderUserData() {
        com.parse.ParseUser user = ParseUser.getCurrentUser();
        // Lookup navigation view
        NavigationView navigationView = (NavigationView) findViewById(R.id.nvView);
        View headerLayout = navigationView.getHeaderView(0);
        TextView tvUserName = (TextView) headerLayout.findViewById(R.id.tvUserName);
        String userName = user.getUsername();
        if (userName == null) {
            tvUserName.setText("(Anonymous user)");
        } else {
            tvUserName.setText(user.getUsername());
        }
    }

    public void refreshMyDeals() {
        // after creating a deal, show My Deals with the new deal on top
        if (viewPager.getCurrentItem() != 1) {
            viewPager.setCurrentItem(1);
        }
        MyDealsFragment myDealsFragment = (MyDealsFragment) dealsFragmentPagerAdapter.getRegisteredFragment(1);
        if (myDealsFragment != null) myDealsFragment.getDeals(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ParseInterface.RESULT_CODE_FACEBOOK) {
            ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
        }

        if (requestCode == ParseInterface.RESULT_CODE_LOGIN || requestCode == ParseInterface.RESULT_CODE_FACEBOOK) {
            // Put username on navigation drawer header now that user is logged in
            setHeaderUserData();
        }

        //createSampleDeals();
        //ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);

    }
    private void createSampleDeals() {


        //TODO : this is to demonstrate how to use the getDeals and createDeal functions, remove this once seen.

        String dealValue = "51%";
        String dealAbstract = "All ice cream pints half price";
        String dealDescription = "April Fools Day is half-price day for all one-pint containers of ice cream";
        String dealRestrictions = "April 1, 2016 only, pint containers only";
        String dealExpiry = "April 1, 2016";
        LatLng latLang = new LatLng(gpsHelper.getLatitude(), gpsHelper.getLongitude());
        String storeName = "New Leaf Market";
        String storeAbstract = "";
        String storeDescription = "";
        String storeLogo = "";
        String storePic = "";

        // createDeal
        DealModel dealObj = pi.createDealObject(dealValue, dealAbstract, dealDescription,
                dealRestrictions, dealExpiry,

                new LatLng(gpsHelper.getLatitude(), gpsHelper.getLongitude()),

                storeName, storeAbstract, storeDescription,
                storeLogo, storePic
        );
        pi.publishDeal(dealObj);
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


        // getDeals
        //pi.getDeals(dealModelArrayList, nfy);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_items, menu);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()){
            case R.id.action_create_deal:
                Intent intent = new Intent(DealsActivity.this, CreatDealActivity.class);
                startActivityForResult(intent, REQUEST_CODE_CREATE_DEAL);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar,
                R.string.drawer_open, /* "open drawer" description for accessibility */
                R.string.drawer_close /* "close drawer" description for accessibility */
        ) {

            public void onDrawerOpened(View drawerView) {
                // I'm sticking this here because although when someone is logging in or creating
                // an account in the first place I can get their name and set the navdrawer header
                // in onActivityResult, but when Parse is getting the user info from LocalDatastore
                // I'm not finding a callback or other place where I can be sure that that will
                // have completed before calling getCurrentUser
                setHeaderUserData();            }
        };
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        switch(menuItem.getItemId()) {
            case R.id.nav_settings:
                break;
            case R.id.nav_logout:
                // log user out
                com.parse.ParseUser user = ParseUser.getCurrentUser();
                ParseUser.logOut();
                // restart the app to bring up the login/register activity and reload
                // the deals lists for the next user
                Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage( getBaseContext().getPackageName() );
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                System.exit(0);
            default:
        }
        mDrawer.closeDrawers();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }

}

