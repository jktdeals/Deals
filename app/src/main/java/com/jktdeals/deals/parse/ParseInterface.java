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
import com.parse.ParseACL;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseImageView;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.interceptors.ParseLogInterceptor;
import com.parse.ui.ParseLoginBuilder;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by kartikkulkarni on 3/4/16.
 */
public class ParseInterface {

    public static int RESULT_CODE_FACEBOOK = 27;
    public static int RESULT_CODE_LOGIN = 28;
    private static ParseInterface singleton = null;
    private static String TAG = "ParseInterface";
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

    public static void populateImageView(ImageView imageView, ParseFile photoFile) {

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

    private void init() {

        // Register your parse models here
        ParseObject.registerSubclass(DealModel.class);
        Parse.enableLocalDatastore(context);


        // set applicationId and server based on the values in the Heroku settings.
        // any network interceptors must be added with the Configuration Builder given this syntax
        Parse.initialize(new Parse.Configuration.Builder(context.getApplicationContext())
                // .applicationId("crowdeal3") // should correspond to APP_ID env variable
                .applicationId("gcmtestparse")
                .addNetworkInterceptor(new ParseLogInterceptor())
                        // .server("https://parseapp3.herokuapp.com/parse/").build());
                .server("https://gcmtestparse.herokuapp.com/parse/").build());

        // Need to register GCM token
        ParseInstallation.getCurrentInstallation().saveInBackground();

        ParseFacebookUtils.initialize(context.getApplicationContext (), RESULT_CODE_FACEBOOK);


        // User login
        //ParseUser.logOut();
        if (ParseUser.getCurrentUser() != null) { // start with existing user
            startWithCurrentUser();
        } else { // If not logged in, login as a new anonymous user
            loginNonAnonymous();
            //loginFBLink(new ParseUser());
            //loginAnonymous();
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

    public void publishDeal(final DealModel dealObject) {

        dealObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                //Toast.makeText(MainActivity.this, "Successfully created message on Parse",
                //        Toast.LENGTH_SHORT).show();

                Log.d(TAG, "successfully saved");

            }

        });
    }

    public void updateDeal(final DealModel dealObject) {
        publishDeal(dealObject);
    }

    public void publishDealonCreate(DealModel dealObject) {

        ParseACL acl = new ParseACL();
        acl.setPublicReadAccess(true);
        acl.setPublicWriteAccess(true);
        dealObject.setACL(acl);
        dealObject.setUser(ParseUser.getCurrentUser());

        dealObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                //Toast.makeText(MainActivity.this, "Successfully created message on Parse",
                //        Toast.LENGTH_SHORT).show();

                Log.d(TAG, "successfully saved");

                // when a deal is created, update My Deals
                DealsActivity dealsActivity = context;
                dealsActivity.refreshMyDeals();

            }

        });

        HashMap<String, String> test = new HashMap<>();
        test.put("alert", "testing");

        ParseCloud.callFunctionInBackground("pushChannelTest", test);
    }

    public void deleteDeal(DealModel dealObject) {

        try {
            dealObject.delete();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private void loginNonAnonymous() {

        ParseLoginBuilder builder = new ParseLoginBuilder(context);
        builder.setFacebookLoginEnabled(true);
        context.startActivityForResult(builder.build(), RESULT_CODE_LOGIN);
    }

    private void loginFB() {


        ParseFacebookUtils.logInWithReadPermissionsInBackground(context, null, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                if (user == null) {
                    Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                } else if (user.isNew()) {
                    Log.d("MyApp", "User signed up and logged in through Facebook!");
                } else {
                    Log.d("MyApp", "User logged in through Facebook!");
                }
            }
        });
    }

    private void loginFBLink(ParseUser user) {


        ParseFacebookUtils.logInWithReadPermissionsInBackground(context, null, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                if (user == null) {
                    Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                } else if (user.isNew()) {
                    Log.d("MyApp", "User signed up and logged in through Facebook!");
                } else {
                    Log.d("MyApp", "User logged in through Facebook!");
                }
            }
        });
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

    public DealModel lookupById(String id) {
        List<DealModel> dealList = null;
        List<String> idList = new ArrayList<String>();
        idList.add(id);

        ParseQuery<DealModel> query = ParseQuery.getQuery(DealModel.class);
        query.whereEqualTo("objectId", id);


        // Execute query to fetch all messages from Parse asynchronously
        // This is equivalent to a SELECT query with SQL
        try {
            dealList = query.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (dealList != null) {
            if (dealList.size() != 0) {
                return dealList.get(0);
            }
        }
        return null;
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

    public void getDealsPaged(final List<DealModel> dealList, final dealLoadNotifier nfy, int pageSize, int pageNumber) {
        getDealsPaged_1(dealList, nfy, pageSize, pageNumber, null, null);

    }

    public void getDealsNear(final List<DealModel> dealList, final dealLoadNotifier nfy, int pageSize, int pageNumber, LatLng latLng) {
        getDealsPaged_1(dealList, nfy, pageSize, pageNumber, null, latLng);

    }

    public void getDealsLiked(final List<DealModel> dealList, final dealLoadNotifier nfy, int pageSize, int pageNumber) {
        JSONArray currentLikedDeals = ParseUser.getCurrentUser().getJSONArray(DealModel.LIKED_DEALS);
        if (currentLikedDeals == null) {
            currentLikedDeals = new JSONArray();
        }
        ArrayList<String> whereList = new ArrayList();

        for (int i = 0; i < currentLikedDeals.length(); i++) {
            try {
                String objId = null;
                objId = currentLikedDeals.getString(i);
                if (objId != null) {
                    whereList.add(objId);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        getDealsPaged_1(dealList, nfy, pageSize, pageNumber, whereList, null);

    }

    // Query messages from Parse so we can load them into the chat adapter
    public void getDealsPaged_1(final List<DealModel> dealList, final dealLoadNotifier nfy,
                                int pageSize, int pageNumber, ArrayList<String> whereList, LatLng latLng) {

        // Construct query to execute
        ParseQuery<DealModel> query = ParseQuery.getQuery(DealModel.class);
        // Configure limit and sort order
        query.setLimit(pageSize);
        query.orderByDescending("createdAt");
        query.setSkip(pageSize * pageNumber);
        if (whereList != null) {
            query.whereContainedIn("objectId", whereList);
        }

        if (latLng != null) {
            query.whereNear(DealModel.LAT_LANG, new ParseGeoPoint(latLng.latitude, latLng.longitude));
        }
        // Execute query to fetch all messages from Parse asynchronously
        // This is equivalent to a SELECT query with SQL
        query.findInBackground(new FindCallback<DealModel>() {
            public void done(List<DealModel> deals, ParseException e) {
                if (e == null) {

                    dealList.clear();
                    dealList.addAll(deals);
                    Log.d(TAG, "loaded: " + deals.size());
                    nfy.notifyLoad(deals.size());


                } else {
                    Log.e(TAG, "Error Loading Messages" + e);
                    nfy.notifyLoad(0);

                }
            }
        });
    }


    public void updateDealImage(DealModel obj, String imageName, Bitmap bmp) {

        ParseFile photoFile = saveScaledPhoto(imageName, bmp);

        obj.setDealPic(imageName, photoFile);
        publishDeal(obj);

    }

    public void updateDealImage(DealModel obj, String imageName, byte[] imageBytes) {

        ParseFile photoFile = null;
        saveScaledPhoto(imageName, imageBytes, photoFile);

        obj.setDealPic(imageName, photoFile);
        publishDeal(obj);

    }

    /*
  * ParseQueryAdapter loads ParseFiles into a ParseImageView at whatever size
  * they are saved. Since we never need a full-size image in our app, we'll
  * save a scaled one right away.
  */
    private ParseFile saveScaledPhoto(final String imageName, Bitmap bmp) {

        // Resize photo from camera byte array
        Bitmap mealImage = bmp;
        Bitmap mealImageScaled = Bitmap.createScaledBitmap(mealImage, 200, 200
                * mealImage.getHeight() / mealImage.getWidth(), false);

        // Override Android default landscape orientation and save portrait
        Matrix matrix = new Matrix();
        Bitmap rotatedScaledMealImage = Bitmap.createBitmap(mealImageScaled, 0,
                0, mealImageScaled.getWidth(), mealImageScaled.getHeight(),
                matrix, true);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        rotatedScaledMealImage.compress(Bitmap.CompressFormat.JPEG, 100, bos);

        byte[] scaledData = bos.toByteArray();

        // Save the scaled image to Parse
        ParseFile photoFile = new ParseFile(imageName, scaledData);
        photoFile.saveInBackground(new SaveCallback() {

            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Failed saving image: " + imageName + " " + e);

                } else {
                    Log.d(TAG, "Succesfully saved image: " + imageName);
                }
            }
        });
        return photoFile;
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

    public void likeDeal(DealModel deal, boolean like) {
        if (like) {
            deal.likeIt();
        } else {
            deal.unLikeIt();
        }
        publishDeal(deal);
        try {
            ParseUser.getCurrentUser().save();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public interface dealLoadNotifier {

        void notifyLoad(int noOfItems);
    }

}
