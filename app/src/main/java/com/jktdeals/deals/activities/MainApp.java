package com.jktdeals.deals.activities;

/**
 * Created by kartikkulkarni on 3/21/16.
 */

import android.app.Application;

import com.jktdeals.deals.models.DealModel;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.interceptors.ParseLogInterceptor;

public class MainApp extends Application {

    private static final String PARSE_APP_ID = "myAppId";
    private static final String PARSE_CLOUD_SERVER_URL = "http://myherokuapp.herokuapp.com/parse/";

    // Make sure to update gcm_sender_id in strings.xml!!

    @Override
    public void onCreate() {
        super.onCreate();
/*
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(PARSE_APP_ID)
                .clientKey(null) // no client key needed in Parse open source
                .server(PARSE_CLOUD_SERVER_URL) // do not forget the URL needs to end with a trailing slash
                .addNetworkInterceptor(new ParseStethoInterceptor())
                .addNetworkInterceptor(new ParseLogInterceptor())
                .build());

        // Need to register GCM token
        ParseInstallation.getCurrentInstallation().saveInBackground();*/


        // Register your parse models here
        ParseObject.registerSubclass(DealModel.class);
        Parse.enableLocalDatastore(getApplicationContext());


        // set applicationId and server based on the values in the Heroku settings.
        // any network interceptors must be added with the Configuration Builder given this syntax
        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                // .applicationId("crowdeal3") // should correspond to APP_ID env variable
                .applicationId("gcmtestparse")
                .addNetworkInterceptor(new ParseLogInterceptor())
                        // .server("https://parseapp3.herokuapp.com/parse/").build());
                .server("https://gcmtestparse.herokuapp.com/parse/").build());

        // Need to register GCM token
        ParseInstallation.getCurrentInstallation().saveInBackground();


    }
}
