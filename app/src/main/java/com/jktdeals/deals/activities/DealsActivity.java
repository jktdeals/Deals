package com.jktdeals.deals.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.maps.model.LatLng;
import com.jktdeals.deals.R;
import com.jktdeals.deals.adapters.DealsFragmentPagerAdapter;
import com.jktdeals.deals.fragments.MyDealsFragment;
import com.jktdeals.deals.fragments.NearMeFragment;
import com.jktdeals.deals.helpers.GPSHelper;
import com.jktdeals.deals.models.DealModel;
import com.jktdeals.deals.parse.ParseInterface;
import com.jktdeals.deals.services.NotificationService;
import com.jktdeals.deals.utility.Constants;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import java.util.ArrayList;

public class DealsActivity extends AppCompatActivity {
    private final int REQUEST_CODE_CREATE_DEAL = 20;
    public ViewPager viewPager;
    public DealsFragmentPagerAdapter dealsFragmentPagerAdapter;
    private DrawerLayout mDrawer;
    private NavigationView nvDrawer;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;
    private ParseInterface pi;
    private ArrayList<DealModel> dealModelArrayList;
    private GPSHelper gpsHelper;
    private ArrayList<DealModel> newDeals;

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
        newDeals = new ArrayList<DealModel>();

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // get location permissions from Android 6/M devices
            getLocationPermissions();
            // and with Android 6/M you also need permission to draw
            // overlay windows like the chathead/notifications
            getOverlayPermission();
        }
    }

    private void getLocationPermissions() {
        // get location permissions from Android 6/M devices
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            // if we don't have permission, and it's an Android M phone,
            // request permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    Constants.MY_PERMISSIONS_ACCESS_FINE_LOCATION);
        }

        permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            // if we don't have permission, and it's an Android M phone,
            // request permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    Constants.MY_PERMISSIONS_ACCESS_COARSE_LOCATION);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void getOverlayPermission() {
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, Constants.MY_PERMISSIONS_DRAW_OVERLAYS);
        }
    }

    // `onPostCreate` called when activity start-up is complete after `onStart()`
    // NOTE! Make sure to override the method with only a single `Bundle` argument
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        processStopService(NotificationService.TAG);
        //Toast.makeText(this, "on restarting", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        processStopService(NotificationService.TAG);
        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);

    }

    private void setHeaderUserData() {
        com.parse.ParseUser user = ParseUser.getCurrentUser();
        // Lookup navigation view
        NavigationView navigationView = (NavigationView) findViewById(R.id.nvView);
        View headerLayout = navigationView.getHeaderView(0);
        TextView tvUserName = (TextView) headerLayout.findViewById(R.id.tvUserName);
        tvUserName.setText(pi.getDisplayName());
    }

    public void refreshMyDeals() {
        // after creating a deal, show My Deals with the new deal on top
        if (viewPager.getCurrentItem() != 1) {
            viewPager.setCurrentItem(1);
        }
        MyDealsFragment myDealsFragment = (MyDealsFragment) dealsFragmentPagerAdapter.getRegisteredFragment(1);
        if (myDealsFragment != null) myDealsFragment.getDeals(false);
    }

    public void refreshNearMe() {
        // after creating a deal, show Near Me with the new deal included
        if (viewPager.getCurrentItem() != 0) {
            viewPager.setCurrentItem(0);
        }
        NearMeFragment nearMeFragment = (NearMeFragment) dealsFragmentPagerAdapter.getRegisteredFragment(0);
        if (nearMeFragment != null) nearMeFragment.getDeals(false);
    }

    public void focusMapOnDeal(DealModel deal) {
        // the user tapped Map on the Deal Detail fragment
        // switch to the Near Me tab if it's not already selected
        if (viewPager.getCurrentItem() != 0) {
            viewPager.setCurrentItem(0);
        }
        NearMeFragment nearMeFragment = (NearMeFragment) dealsFragmentPagerAdapter.getRegisteredFragment(0);
        // and focus the map on the deal the user had selected for Deal Detail
        nearMeFragment.focusMapCamara(deal);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items, menu);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.action_create_deal:
                Intent intent = new Intent(DealsActivity.this, CreatDealActivity.class);
                startActivityForResult(intent, REQUEST_CODE_CREATE_DEAL);
                return true;
            case R.id.action_add_notification: //add notification and head chat
                createServiceNotification();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void createServiceNotification() {
        try {

            if (isMyServiceRunning(NotificationService.class)) {
                Log.v("NotificaitonService", "Notification service is already running");
            } else {
                createSampleDealsForNotification();
                Bundle bundle = new Bundle();
                int newDealsCount = 0;
                for (int i = 0; i < newDeals.size(); i++) {
                    String nameField = "deal" + i;
                    bundle.putString(nameField + "Abstract", newDeals.get(i).getDealAbstract());
                    bundle.putString(nameField + "Description", newDeals.get(i).getDealDescription());
                    bundle.putString(nameField + "Value", newDeals.get(i).getDealValue());
                    bundle.putString(nameField + "Category", newDeals.get(i).getCATSTRING());//method to get single

                    //JSONArray cats = newDeals.get(i).getCategories();
                    //ArrayList<DealModel.Category> category = newDeals.get(i).getCategoriesList();

                    newDealsCount++;
                }

                bundle.putInt("newDealsCount", newDealsCount);
                processStartService(NotificationService.TAG, bundle);
                createNotification(); //it will add bar notification

                newDeals.clear(); //clear new deals and waiting for new ones to cone
            }
        } catch (Exception ex) {
            Log.v("ChatHead", ex.getMessage());
        }
    }

    public void createNotification() {
        try {
            Intent intent = new Intent(DealsActivity.this, DealsActivity.class);
            intent.putExtra("fromNotification", true);
            PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

            Notification n = new Notification.Builder(this)
                    .setContentTitle("New Deals")
                    .setContentText("Touch to view New Deals")
                            //.setSmallIcon(R.drawable.head)
                    .setSmallIcon(R.drawable.ic_circle_d)
                    .setContentIntent(pIntent)
                    .setAutoCancel(true)
                    .build();

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(0, n);
        } catch (IllegalArgumentException ex) {
            Log.v("CreatingNotification", ex.getMessage());
        } catch (Exception ex) {
            Log.v("CreatingNotification", ex.getMessage());
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void processStartService(final String tag, Bundle bundle) {
        try {
            Intent intent = new Intent(this, NotificationService.class);
            intent.putExtras(bundle);
            intent.addCategory(tag);
            startService(intent);
        } catch (Exception ex) {
            Log.v("Service", ex.getMessage());
        }
    }

    private void processStopService(final String tag) {
        try {
            Intent intent = new Intent(this, NotificationService.class);
            intent.addCategory(tag);
            stopService(intent);
        } catch (Exception ex) {
            Log.v("Service", ex.getMessage());
        }
    }

    private void createSampleDealsForNotification() {
        String dealValue = "51%";
        String dealAbstract = "All ice cream pints half price";
        String dealDescription = "April Fools Day is half-price day for all one-pint containers of ice cream";
        String dealRestrictions = "April 1, 2016 only, pint containers only";
        String dealExpiry = "April 1, 2016";
        //LatLng latLang = new LatLng(gpsHelper.getLatitude(), gpsHelper.getLongitude());
        String storeName = "New Leaf Market";
        String storeAbstract = "";
        String storeDescription = "";
        String storeLogo = "";
        String storePic = "";

        // createDeal
        DealModel dealObj01 = pi.createDealObject(dealValue, dealAbstract, dealDescription,
                dealRestrictions, dealExpiry,

                new LatLng(0, 0),

                storeName, storeAbstract, storeDescription,
                storeLogo, storePic
        );

        DealModel dealObj02 = pi.createDealObject("5.00", "Burrito for less", "50% off",
                dealRestrictions, dealExpiry,

                new LatLng(0, 0),

                storeName, storeAbstract, storeDescription,
                storeLogo, storePic
        );

        dealObj01.setCATSTRING("Home Services");
        dealObj02.setCATSTRING("Restaurant");
        newDeals.add(dealObj01);
        newDeals.add(dealObj02);

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
                setHeaderUserData();
            }
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
        switch (menuItem.getItemId()) {
            case R.id.nav_settings:
                break;
            case R.id.nav_logout:
                // log user out
                com.parse.ParseUser user = ParseUser.getCurrentUser();
                ParseUser.logOut();
                // restart the app to bring up the login/register activity and reload
                // the deals lists for the next user
                Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
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

