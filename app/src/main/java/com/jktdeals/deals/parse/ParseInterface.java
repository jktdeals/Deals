package com.jktdeals.deals.parse;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.jktdeals.deals.models.DealModel;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.interceptors.ParseLogInterceptor;

import java.util.List;

/**
 * Created by kartikkulkarni on 3/4/16.
 */
public class ParseInterface {

    private static ParseInterface singleton = null;
    private String TAG = "Parse";
    private Context context;
    private int MAX_CHAT_MESSAGES_TO_SHOW = 50;

    private ParseInterface(Context context) {
        this.context = context;
        init();
    }

    public static ParseInterface getInstance(Context context) {

        if (singleton != null) {
            return singleton;
        } else {
            singleton = new ParseInterface(context);
        }
        return singleton;

    }

    private void init() {

        // Register your parse models here
        ParseObject.registerSubclass(DealModel.class);

        // set applicationId and server based on the values in the Heroku settings.
        // any network interceptors must be added with the Configuration Builder given this syntax
        Parse.initialize(new Parse.Configuration.Builder(context)
                .applicationId("crowdeal") // should correspond to APP_ID env variable
                .addNetworkInterceptor(new ParseLogInterceptor())
                .server("https://crowdeal.herokuapp.com/parse/").build());

        // User login
        if (ParseUser.getCurrentUser() != null) { // start with existing user
            startWithCurrentUser();
        } else { // If not logged in, login as a new anonymous user
            login();
        }

    }

    public DealModel createDealObject(String dealValue, String dealAbstract, String dealDescription,
                                      String dealRestrictions, String dealExpiry, String dealPic,

                                      LatLng latLang,

                                      String storeName, String storeAbstract, String storeDescription,
                                      String storeLogo, String storePic

    ) {
        //ParseObject dealObject = ParseObject.create("DealModel");
        DealModel dealObject = new DealModel();

        dealObject.setDealValue(dealValue);
        dealObject.setDealAbstract(dealAbstract);
        dealObject.setDealDescription(dealDescription);
        dealObject.setDealRestrictions(dealRestrictions);
        dealObject.setDealExpiry(dealExpiry);
        dealObject.setDealPic(dealPic);

        dealObject.setLatLang(latLang);

        dealObject.setStoreName(storeName);
        dealObject.setStoreAbstract(storeAbstract);
        dealObject.setStoreDescription(storeDescription);
        dealObject.setStoreLogo(storeLogo);
        dealObject.setStorePic(storePic);


        return dealObject;
    }

    public void publishDeal(DealModel dealObject) {

        dealObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                //Toast.makeText(MainActivity.this, "Successfully created message on Parse",
                //        Toast.LENGTH_SHORT).show();

                Log.d(TAG, "successfully saved");
            }

        });

    }

    // Create an anonymous user using ParseAnonymousUtils and set sUserId
    private void login() {
        ParseAnonymousUtils.logIn(new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Anonymous login failed: ", e);
                } else {
                    Log.e(TAG, "Anonymous login success: ", e);

                    startWithCurrentUser();
                }
            }
        });
    }

    // Get the userId from the cached currentUser object
    private void startWithCurrentUser() {
        // TODO:
    }

    // Query messages from Parse so we can load them into the chat adapter
    public void getDeals(final List<DealModel> dealList, final dealLoadNotifier nfy) {

        // Construct query to execute
        ParseQuery<DealModel> query = ParseQuery.getQuery(DealModel.class);
        // Configure limit and sort order
        query.setLimit(MAX_CHAT_MESSAGES_TO_SHOW);
        query.orderByDescending("createdAt");
        // Execute query to fetch all messages from Parse asynchronously
        // This is equivalent to a SELECT query with SQL
        query.findInBackground(new FindCallback<DealModel>() {
            public void done(List<DealModel> deals, ParseException e) {
                if (e == null) {
                    dealList.clear();
                    dealList.addAll(deals);
                    nfy.notifyLoad(deals.size());

                } else {
                    Log.e(TAG, "Error Loading Messages" + e);
                }
            }
        });
    }

    public interface dealLoadNotifier {

        void notifyLoad(int noOfItems);
    }

}
