package com.jktdeals.deals.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.jktdeals.deals.R;
import com.jktdeals.deals.Yelp.Yelp;
import com.jktdeals.deals.fragments.DatePickerFragment;
import com.jktdeals.deals.helpers.GPSHelper;
import com.jktdeals.deals.models.DealModel;
import com.jktdeals.deals.parse.ParseInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CreatDealActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public static final int CANCEL_CODE = 9;
    private static final int TAKE_PHOTO_CODE = 1;
    private static final int PICK_PHOTO_CODE = 2;
    private static final int CROP_PHOTO_CODE = 3;
    private static final int POST_PHOTO_CODE = 4;
    private static final int SET_LOCATION_REQUEST = 50;
    private static final int SET_LOCATION_RESULT = 100;
    public final String APP_TAG = "MyCustomApp";
    public String photoFileName = "photo.jpg";


    //private Uri photoUri;
    //private Bitmap photoBitmap;
    ParseInterface client;
    GPSHelper gpsHelper;
    EditText etDealName;
    EditText etDealValue;
    EditText etDealRestrictions;
    EditText etDealDescriptions;
    TextView tvExpirationDateDisplay;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yy/MM/dd");
    Spinner spDealCategory;
    Date expirationDate;
    // Location propertioes
    private boolean locationSet = false;
    private LatLng latLng;
    private String placeName;
    private String placeAddress;
    private String placeUri;
    private String placePhoneNumber;
    private float placeRatings;
    private String method = "newDeal";

    private String yelpMobileUrl;
    private double yelpRating;
    private String yelpSmallRatingImgUrl;
    private String yelpSnipUrl;

    private DealModel dealToBeEdited;
    private Uri photoUri;
    private Bitmap photoBitmap;

    private Yelp yelp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creat_deal);

        loadDisplaySettings();

        //will check if its a edit or  ========================

        Bundle bundle = this.getIntent().getExtras();
        if(bundle != null){
            this.method = "editDeal";
            this.dealToBeEdited = client.lookupById(bundle.getString("id"));
            //dealToBeEdited = bundle.getParcelable("DealModel");

            //this.latLng = bundle.getParcelable("latLng");
            this.latLng = this.dealToBeEdited.getLatLang();
            //this.placeName = bundle.getString("storeName");
            this.placeName = this.dealToBeEdited.getStoreName();
            this.locationSet = true; //will get loc properties from current dealtobeedited

            //etDealName.setText(bundle.getString("abstract"));
            etDealName.setText(this.dealToBeEdited.getDealAbstract());
            //etDealValue.setText(bundle.getString("value"));
            etDealValue.setText(this.dealToBeEdited.getDealValue());

            //etDealRestrictions.setText(bundle.getString("restriction"));
            etDealRestrictions.setText(this.dealToBeEdited.getDealRestrictions());

            //etDealDescriptions.setText(bundle.getString("description"));
            etDealDescriptions.setText(this.dealToBeEdited.getDealDescription());

            //tvExpirationDateDisplay.setText(bundle.getString("expirationDate"));
            tvExpirationDateDisplay.setText(this.dealToBeEdited.getDealExpiry());

            //Bitmap pic = dealToBeEdited.getDealPic();
            //spDealCategory.setSelection(this.getCotegoryIndex(dealToBeEdited.getCategories().get(0).toString()));

        }

    }

    private int getCotegoryIndex(String value){
        switch (value){
            case "Cafe": return 0;
            case "Bar": return 1;
            case "Restaurant": return 2;
            case "Hotel": return 3;
            case "Beauty": return 4;
            case "Entertainment": return 5;
            case "Pets": return 6;
            case "Activities": return 7;
            case "Massage": return 8;
            case "Apparel": return 9;
            case "Groceries": return 10;
            case "Local_Services": return 11;
            case "Home_Services": return 12;
            case "Health": return 13;

        }

        return 0;
    }

    private void loadDisplaySettings(){

        client = ParseInterface.getInstance(this);
        gpsHelper = new GPSHelper(getApplicationContext());
        photoBitmap = null;
        yelp = Yelp.getYelp(this);

        etDealName = (EditText) findViewById(R.id.etDealName);
        etDealValue = (EditText) findViewById(R.id.etDealValue);
        etDealRestrictions = (EditText) findViewById(R.id.etDealRestrictions);
        etDealDescriptions = (EditText) findViewById(R.id.etDealDescription);
        tvExpirationDateDisplay = (TextView) findViewById(R.id.tvExpirationDateDisplay);
        spDealCategory = (Spinner) findViewById(R.id.spDealCategory);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.deals_category, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDealCategory.setAdapter(adapter);

        final AlertDialog.Builder ad = new AlertDialog.Builder(this);
        //ad.setTitle("What do you Like ?");
        ad.setItems(R.array.photo_action, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                switch (arg1) {
                    case 0: //take picture using camera
                        onTakePicture();
                        break;

                    case 1: //load existing photo
                        onSelectPicture();
                        break;
                }
            }
        });

        final Button btn = (Button) findViewById(R.id.btDealPhoto);
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                ad.show();
            }
        });

        final Button btn1 = (Button) findViewById(R.id.btPostDeal);
        btn1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                onPostDeal();
            }
        });

        final Button btn2 = (Button) findViewById(R.id.btCancelDeal);
        btn2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                onCancel();
            }
        });
    }

    public void onTakePicture(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getPhotoFileUri(photoFileName)); // set the image file name

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, TAKE_PHOTO_CODE);
        }
    }

    public void onSelectPicture(){
        // Take the user to the gallery app
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, PICK_PHOTO_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == TAKE_PHOTO_CODE) {
                Uri photoUri = getPhotoFileUri(photoFileName);
                cropPhoto(photoUri);
            } else if (requestCode == PICK_PHOTO_CODE) {
                // Extract the photo that was just picked from the gallery

                Uri photoUri = data.getData();
                // Do something with the photo based on Uri
                //Bitmap selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                // Load the selected image into a preview
                //ImageView ivPreview = (ImageView) findViewById(R.id.ivPreview);
                //ivPreview.setImageBitmap(selectedImage);

                // Call the method below to trigger the cropping
                cropPhoto(photoUri);
            } else if (requestCode == CROP_PHOTO_CODE) {
                //postDeal();
                photoBitmap = data.getParcelableExtra("data");
                //startPreviewPhotoActivity();
            } else if (requestCode == POST_PHOTO_CODE) {
                //reloadPhotos();
            }else if (requestCode == SET_LOCATION_REQUEST){


            }

        }
        else if(resultCode == SET_LOCATION_RESULT){ //will suggested google place properties
            switch (requestCode){
                case SET_LOCATION_REQUEST:
                    try{
                        Bundle bundle = data.getExtras();
                        latLng = bundle.getParcelable("latLng");
                        placeName = bundle.getString("name");
                        placeAddress = bundle.getString("address");
                        placeUri = bundle.getString("uri");
                        placePhoneNumber = bundle.getString("phoneNumber");
                        placeRatings = bundle.getFloat("ratings");
                        locationSet = true;

                        final String category = spDealCategory.getSelectedItem().toString();
                        new AsyncTask<Void, Void, String>() {
                            @Override
                            protected String doInBackground(Void... params) {
                                String businesses = yelp.search(placeName, placeAddress, "1");
                                try {
                                    return processJson(businesses);
                                } catch (JSONException e) {
                                    return businesses;
                                }
                            }

                            @Override
                            protected void onPostExecute(String result) {
                                //yelpUrl = result;
                                Log.v("YELP", result);//mSearchResultsText.setText(result);
                            }
                        }.execute();
                    }
                    catch (Exception ex){
                        Log.v("Execption", ex.getMessage().toString());
                    }

                    break;

            }
        }
    }

    String processJson(String jsonStuff) throws JSONException {
        Log.v("ResultRaw", jsonStuff.toString());
        JSONObject json = new JSONObject(jsonStuff);
        JSONArray businesses = json.getJSONArray("businesses");
        ArrayList<String> businessNames = new ArrayList<String>(businesses.length());
        ArrayList<String> mobileUrl = new ArrayList<String>(businesses.length());
        for (int i = 0; i < businesses.length(); i++) {
            JSONObject business = businesses.getJSONObject(i);
            Log.v("Result1", business.toString());

            yelpMobileUrl = business.getString("mobile_url");
            yelpRating = business.getDouble("rating");
            //yelpSnipUrl = business.getString("snippet_image_url");
            yelpSnipUrl = business.getString("image_url");
            yelpSmallRatingImgUrl = business.getString("rating_img_url_small");

            businessNames.add(business.getString("name"));
            mobileUrl.add(business.getString("mobile_url"));
        }
        return mobileUrl.get(0);
    }

    public void SetExpirationDate(View view) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        final Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        tvExpirationDateDisplay.setText(dateFormat.format(c.getTime()));
        this.expirationDate = c.getTime();
    }

    public void onCancel () {
        Intent cancelIntent = new Intent(this, DealsActivity.class);
        startActivity(cancelIntent);
    }

    public void onPostDeal() {

        DealModel newDeal = null;
        if (this.method.equals("newDeal")) {
            newDeal = new DealModel();
        } else {
            newDeal = this.dealToBeEdited;
        }

        newDeal.setDealAbstract(etDealName.getText().toString());
        newDeal.setDealValue(etDealValue.getText().toString());
        newDeal.setDealDescription(etDealDescriptions.getText().toString());
        newDeal.setDealRestrictions(etDealRestrictions.getText().toString());
        newDeal.setDealExpiry(tvExpirationDateDisplay.getText().toString());
        newDeal.setDealYelpMobileUrl(yelpMobileUrl);
        newDeal.setDealYelpRating((float)yelpRating);
        newDeal.setDealYelpSnipUrl(yelpSnipUrl);
        newDeal.setDDealYelpRatingImageUrl(yelpSmallRatingImgUrl);

        //newDeal.setDealPic(photoUri.toString()); // or can pass photoBitmapt is already load with the pic
        if (photoBitmap != null){
            client.updateDealImage(newDeal,"dealpic",photoBitmap);
        }

        if(locationSet){
            newDeal.setLatLang(latLng);
            newDeal.setStoreName(placeName);
        }else {
            if(gpsHelper.isGPSenabled()){
                gpsHelper.getMyLocation();

                LatLng latlng = new LatLng(gpsHelper.getLatitude(), gpsHelper.getLongitude());
                newDeal.setLatLang(latlng);
            }
        }

        try{

            switch (this.method){
                case "editDeal":
                    client.updateDeal(newDeal);
                    break;
                case "newDeal":
                    client.publishDealonCreate(newDeal);
                    break;
            }

        }
        catch(Exception ex){
            Toast.makeText(this, "failed to post deal", Toast.LENGTH_SHORT).show();
        }

        Intent intent = new Intent(this, DealsActivity.class);
        setResult(RESULT_OK, intent);
        this.finish();
    }

    public Uri getPhotoFileUri(String fileName) {
        // Only continue if the SD Card is mounted
        if (isExternalStorageAvailable()) {
            // Get safe storage directory for photos
            // Use `getExternalFilesDir` on Context to access package-specific directories.
            // This way, we don't need to request external read/write runtime permissions.
            File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
                Log.d(APP_TAG, "failed to create directory");
            }

            // Return the file target for the photo based on filename
            return Uri.fromFile(new File(mediaStorageDir.getPath() + File.separator + fileName));
        }
        return null;
    }

    private boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    private void cropPhoto(Uri photoUri) {
        //call the standard crop action intent (the user device may not support it)
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        //indicate image type and Uri
        cropIntent.setDataAndType(photoUri, "image/*");
        //set crop properties
        cropIntent.putExtra("crop", "true");
        //indicate aspect of desired crop
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        //indicate output X and Y
        cropIntent.putExtra("outputX", 300);
        cropIntent.putExtra("outputY", 300);
        //retrieve data on return
        cropIntent.putExtra("return-data", true);
        //start the activity - we handle returning in onActivityResult
        startActivityForResult(cropIntent, CROP_PHOTO_CODE);
    }

    public void onLocationSearch(View view) {
        Intent i = new Intent(this, SuggestedPlaces.class );
        startActivityForResult(i, SET_LOCATION_REQUEST);
    }

//    public void onOpenYelp(View view) {
//        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(yelpUrl));
//        startActivity(browserIntent);
//    }
}
