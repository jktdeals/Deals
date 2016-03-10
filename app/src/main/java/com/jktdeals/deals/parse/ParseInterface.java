package com.jktdeals.deals.parse;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;
import com.jktdeals.deals.activities.DealsActivity;
import com.jktdeals.deals.models.DealModel;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.interceptors.ParseLogInterceptor;
import com.parse.ui.ParseLoginBuilder;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * Created by kartikkulkarni on 3/4/16.
 */
public class ParseInterface {

    private static ParseInterface singleton = null;
    private String TAG = "Parse";
    private DealsActivity context;
    private int MAX_CHAT_MESSAGES_TO_SHOW = 50;

    private ParseInterface(Context context) {
        this.context = (DealsActivity) context;
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
        Parse.enableLocalDatastore(context);


        // set applicationId and server based on the values in the Heroku settings.
        // any network interceptors must be added with the Configuration Builder given this syntax
        Parse.initialize(new Parse.Configuration.Builder(context)
                .applicationId("crowdeal") // should correspond to APP_ID env variable
                .addNetworkInterceptor(new ParseLogInterceptor())
                .server("https://crowdeal.herokuapp.com/parse/").build());


        // User login
        //ParseUser.logOut();
        if (ParseUser.getCurrentUser() != null) { // start with existing user
            startWithCurrentUser();
        } else { // If not logged in, login as a new anonymous user
            loginNonAnonymous();
        }

    }

    public DealModel createDealObject(String dealValue, String dealAbstract, String dealDescription,
                                      String dealRestrictions, String dealExpiry,
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

        dealObject.setLatLang(latLang);

        dealObject.setStoreName(storeName);
        dealObject.setStoreAbstract(storeAbstract);
        dealObject.setStoreDescription(storeDescription);
        dealObject.setStoreLogo(storeLogo);
        dealObject.setStorePic(storePic);

        dealObject.setUser(ParseUser.getCurrentUser());


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


    private void loginNonAnonymous() {

        ParseLoginBuilder builder = new ParseLoginBuilder(context);
        context.startActivityForResult(builder.build(), 7);
    }

    // Create an anonymous user using ParseAnonymousUtils and set sUserId
    private void loginAnonymous() {
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
    public void getMyDeals(final List<DealModel> dealList, final dealLoadNotifier nfy) {

        // Construct query to execute
        ParseQuery<DealModel> query = ParseQuery.getQuery(DealModel.class);
        // Configure limit and sort order
        query.setLimit(MAX_CHAT_MESSAGES_TO_SHOW);
        query.orderByDescending("createdAt");
        query.whereEqualTo("userId", ParseUser.getCurrentUser());
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

    public void updateDealImage(DealModel obj, String imageName, byte[] imageBytes) {

        ParseFile photoFile = null;
        saveScaledPhoto(imageName, imageBytes, photoFile);

        obj.setDealPic(imageName, photoFile);
        publishDeal(obj);

    }

    public void populateImageView(ImageView imageView, ParseFile photoFile) {

        final ParseImageView parseImageView = (ParseImageView) imageView;
        parseImageView.setVisibility(View.INVISIBLE);


        if (photoFile != null) {
            parseImageView.setParseFile(photoFile);
            parseImageView.loadInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    parseImageView.setVisibility(View.VISIBLE);
                    if (e != null) {
                        Log.e(TAG, "Failed loading image: " + e);

                    }
                }
            });
        }

    }

    /*
     * ParseQueryAdapter loads ParseFiles into a ParseImageView at whatever size
     * they are saved. Since we never need a full-size image in our app, we'll
     * save a scaled one right away.
     */
    private void saveScaledPhoto(final String imageName, byte[] data, ParseFile photoFile) {

        // Resize photo from camera byte array
        Bitmap mealImage = BitmapFactory.decodeByteArray(data, 0, data.length);
        Bitmap mealImageScaled = Bitmap.createScaledBitmap(mealImage, 200, 200
                * mealImage.getHeight() / mealImage.getWidth(), false);

        // Override Android default landscape orientation and save portrait
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap rotatedScaledMealImage = Bitmap.createBitmap(mealImageScaled, 0,
                0, mealImageScaled.getWidth(), mealImageScaled.getHeight(),
                matrix, true);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        rotatedScaledMealImage.compress(Bitmap.CompressFormat.JPEG, 100, bos);

        byte[] scaledData = bos.toByteArray();

        // Save the scaled image to Parse
        photoFile = new ParseFile(imageName, scaledData);
        photoFile.saveInBackground(new SaveCallback() {

            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Failed saving image: " + imageName + " " + e);

                } else {
                    Log.d(TAG, "Succesfully saved image: " + imageName);
                }
            }
        });
    }


    public interface dealLoadNotifier {

        void notifyLoad(int noOfItems);
    }

}