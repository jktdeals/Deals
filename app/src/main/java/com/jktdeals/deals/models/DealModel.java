package com.jktdeals.deals.models;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by kartikkulkarni on 3/4/16.
 */
@ParseClassName("Deal")
public class DealModel extends ParseObject {

    //User Related
    public static final String USER_ID_KEY = "userId";
    // Deal Related
    public static final String DEAL_VALUE = "dealValue";
    public static final String DEAL_ABSTRACT = "dealAbstract";
    public static final String DEAL_DESCRIPTION = "dealDescription";
    public static final String DEAL_RESTRICTIONS = "dealRestrictions";
    public static final String DEAL_EXPIRY = "dealExpiry";
    public static final String DEAL_PIC = "dealPic1";
    //Location related
    public static final String LAT_LANG = "latLang";
    //Store Related
    public static final String STORE_NAME = "storeName";
    //Location related
    public static final String STORE_ABSTRACT = "storeAbstract";
    public static final String STORE_DESCRIPTION = "storeDescription";
    public static final String STORE_LOGO = "storeLogo";
    public static final String STORE_PIC = "storePic";
    //Likes
    public static final String LIKES_COUNT = "likes";
    public static final String LIKED_DEALS = "likedDeals";
    // Category Related
    public static String CATEGORIES = "categories";
    private static String TAG = "DealModel";


    public ParseUser getUser() {
        return getParseUser(USER_ID_KEY);
    }

    public void setUser(ParseUser parseUser) {
        put(USER_ID_KEY, parseUser);
    }

    public String getDealValue() {
        return getString(DEAL_VALUE);

    }


    public void setDealValue(String dealValue) {
        put(DEAL_VALUE, dealValue);
    }

    public String getDealAbstract() {
        return getString(DEAL_ABSTRACT);
    }

    public void setDealAbstract(String dealAbstract) {
        put(DEAL_ABSTRACT, dealAbstract);
    }

    public String getDealRestrictions() {
        return getString(DEAL_RESTRICTIONS);
    }

    public void setDealRestrictions(String dealRestrictions) {
        put(DEAL_RESTRICTIONS, dealRestrictions);
    }

    public String getDealDescription() {
        return getString(DEAL_DESCRIPTION);
    }

    public void setDealDescription(String dealDescription) {
        put(DEAL_DESCRIPTION, dealDescription);
    }

    public String getDealExpiry() {
        return getString(DEAL_EXPIRY);

    }

    public void setDealExpiry(String dealExpiry) {
        put(DEAL_EXPIRY, dealExpiry);
    }

    public ParseFile getDealPic() {
        return getParseFile(DEAL_PIC);

    }

    public void setDealPic(String dealPicName, ParseFile parseFile) {
        put(DEAL_PIC, parseFile);
    }

    public LatLng getLatLang() {
        ParseGeoPoint parseGeoPoint = (ParseGeoPoint) get(LAT_LANG);
        return new LatLng(parseGeoPoint.getLatitude(), parseGeoPoint.getLongitude());

    }

    public void setLatLang(LatLng latLang) {

        ParseGeoPoint geoPoint = new ParseGeoPoint(latLang.latitude, latLang.longitude);
        put(LAT_LANG, geoPoint);
    }

    public String getStoreAbstract() {
        return getString(STORE_ABSTRACT);

    }

    public void setStoreAbstract(String storeAbstract) {
        put(STORE_ABSTRACT, storeAbstract);
    }

    public String getStoreName() {
        return getString(STORE_NAME);
    }

    public void setStoreName(String storeName) {
        put(STORE_NAME, storeName);
    }

    public String getStoreDescription() {
        return getString(STORE_DESCRIPTION);

    }

    public void setStoreDescription(String storeDescription) {
        put(STORE_DESCRIPTION, storeDescription);
    }

    public String getStorePic() {
        return getString(STORE_PIC);
    }

    public void setStorePic(String storePic) {
        put(STORE_PIC, storePic);
    }

    public String getStoreLogo() {
        return getString(STORE_LOGO);

    }

    public void setStoreLogo(String storeLogo) {
        put(STORE_LOGO, storeLogo);
    }

    public void setCategory(Category category) {
        JSONArray currentCategories = getCategories();
        if (currentCategories == null) {
            currentCategories = new JSONArray();
        }
        currentCategories.put(category);
        put(CATEGORIES, currentCategories);
    }

    public JSONArray getCategories() {
        return getJSONArray(CATEGORIES);

    }

    public int getLikesCount() {
        return getInt(LIKES_COUNT);

    }

    public int incrementLikes() {
        put(LIKES_COUNT, getInt(LIKES_COUNT) + 1);
        return getInt(LIKES_COUNT);

    }

    public void setLikedDeal() {
        JSONArray currentLikedDeals = ParseUser.getCurrentUser().getJSONArray(LIKED_DEALS);
        if (currentLikedDeals == null) {
            currentLikedDeals = new JSONArray();
        }
        currentLikedDeals.put(this.getObjectId());
        ParseUser.getCurrentUser().put(LIKED_DEALS, currentLikedDeals);
        Log.d(TAG, currentLikedDeals.toString());
    }

    public int likeIt() {
        setLikedDeal();
        return incrementLikes();
    }

    public boolean isLiked() {
        JSONArray currentLikedDeals = ParseUser.getCurrentUser().getJSONArray(LIKED_DEALS);
        if (currentLikedDeals != null) {
            Log.d(TAG, currentLikedDeals.toString());

            for (int i = 0; i < currentLikedDeals.length(); i++) {
                try {
                    String objId = null;
                    objId = currentLikedDeals.getString(i);
                    if (this.getObjectId().equals(objId)) {
                        return true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }
        return false;
    }



    public enum Category {
        Cafe, Bar, Restaurant, Hotel, Beauty, Entertainment, Pets, Activities, Massage, Apparel, Groceries, Local_Services, Home_Services, Health

    }


}
