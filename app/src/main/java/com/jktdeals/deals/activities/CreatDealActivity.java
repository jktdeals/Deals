package com.jktdeals.deals.activities;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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
import com.jktdeals.deals.fragments.DatePickerFragment;
import com.jktdeals.deals.helpers.GPSHelper;
import com.jktdeals.deals.models.DealModel;
import com.jktdeals.deals.parse.ParseInterface;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CreatDealActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    public final String APP_TAG = "MyCustomApp";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public String photoFileName = "photo.jpg";

    private static final int TAKE_PHOTO_CODE = 1;
    private static final int PICK_PHOTO_CODE = 2;
    private static final int CROP_PHOTO_CODE = 3;
    private static final int POST_PHOTO_CODE = 4;

    private Uri photoUri;
    private Bitmap photoBitmap;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creat_deal);

        loadDisplaySettings();
    }

    private void loadDisplaySettings(){

        client = ParseInterface.getInstance(this);
        gpsHelper = new GPSHelper(getApplicationContext());

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
            }
        }
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

    public void onPostDeal(View view) {

        DealModel newDeal = new DealModel();
        newDeal.setDealAbstract(etDealName.getText().toString());
        newDeal.setDealValue(etDealValue.getText().toString());
        newDeal.setDealDescription(etDealDescriptions.getText().toString());
        newDeal.setDealRestrictions(etDealRestrictions.getText().toString());
        newDeal.setDealExpiry(tvExpirationDateDisplay.getText().toString());
        //newDeal.setDealPic(photoUri.toString()); // or can pass photoBitmapt is already load with the pic
        if(gpsHelper.isGPSenabled()){
            gpsHelper.getMyLocation();

            LatLng latlng = new LatLng(gpsHelper.getLatitude(), gpsHelper.getLongitude());
            newDeal.setLatLang(latlng);
        }

        try{
            client.publishDeal(newDeal);
        }
        catch(Exception ex){
            Toast.makeText(this, "failed to post deal", Toast.LENGTH_SHORT).show();
        }
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
}
