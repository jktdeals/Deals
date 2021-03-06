package com.jktdeals.deals.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.jktdeals.deals.R;
import com.jktdeals.deals.adapters.PlacesAutoCompleteAdapter;
import com.jktdeals.deals.listeners.RecyclerItemClickListener;
import com.jktdeals.deals.utility.Constants;

public class SuggestedPlaces extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{

    protected GoogleApiClient mGoogleApiClient;
    private static final int SET_LOCATION_RESULT = 100;

    private static final LatLngBounds BOUNDS_INDIA = new LatLngBounds(
            new LatLng(-0, 0), new LatLng(0, 0));

    private EditText mAutocompleteView;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private PlacesAutoCompleteAdapter mAutoCompleteAdapter;
    ImageView delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildGoogleApiClient();
        setContentView(R.layout.activity_add_location);
        mAutocompleteView = (EditText)findViewById(R.id.autocomplete_places);

        try{
            delete=(ImageView)findViewById(R.id.cross);

            mAutoCompleteAdapter =  new PlacesAutoCompleteAdapter(this, R.layout.item_search_row,
                    mGoogleApiClient, BOUNDS_INDIA, null);

            mRecyclerView=(RecyclerView)findViewById(R.id.recyclerView);
            mLinearLayoutManager=new LinearLayoutManager(this);

            //breaks herer

            mRecyclerView.setLayoutManager(mLinearLayoutManager);
            mRecyclerView.setAdapter(mAutoCompleteAdapter);
            delete.setOnClickListener(this);
            mAutocompleteView.addTextChangedListener(new TextWatcher() {

                public void onTextChanged(CharSequence s, int start, int before,
                                          int count) {
                    if (!s.toString().equals("") && mGoogleApiClient.isConnected()) {
                        mAutoCompleteAdapter.getFilter().filter(s.toString());
                    } else if (!mGoogleApiClient.isConnected()) {
                        Toast.makeText(getApplicationContext(), Constants.API_NOT_CONNECTED, Toast.LENGTH_SHORT).show();
                        Log.e(Constants.PlacesTag, Constants.API_NOT_CONNECTED);
                    }

                }

                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {

                }

                public void afterTextChanged(Editable s) {

                }
            });
            mRecyclerView.addOnItemTouchListener(
                    new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            final PlacesAutoCompleteAdapter.PlaceAutocomplete item = mAutoCompleteAdapter.getItem(position);
                            final String placeId = String.valueOf(item.placeId);
                            Log.i("TAG", "Autocomplete item selected: " + item.description);
                        /*
                             Issue a request to the Places Geo Data API to retrieve a Place object with additional details about the place.
                         */

                            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                                    .getPlaceById(mGoogleApiClient, placeId);
                            placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                                @Override
                                public void onResult(PlaceBuffer places) {
                                    if (places.getCount() == 1) {
                                        //Do the things here on Click.....

                                        try{
                                            Intent data = new Intent();
                                            Bundle placeBundle = new Bundle();
                                            Place selectedPlace = places.get(0);
                                            LatLng latLng = selectedPlace.getLatLng();
                                            String name = selectedPlace.getName().toString();
                                            String address = selectedPlace.getAddress().toString();
                                            Uri uri = selectedPlace.getWebsiteUri();
                                            String phoneNumber = selectedPlace.getPhoneNumber().toString();
                                            float ratings = selectedPlace.getRating();

                                            placeBundle.putParcelable("latLng", latLng);
                                            placeBundle.putString("name", name);
                                            placeBundle.putString("address", address);
                                            if(uri != null) placeBundle.putString("uri", uri.toString());
                                            if(phoneNumber != null) placeBundle.putString("phoneNumber",phoneNumber);
                                            placeBundle.putFloat("ratings", ratings);

                                            data.putExtras(placeBundle);
                                            setResult(SET_LOCATION_RESULT, data);
                                            finish();
                                        }
                                        catch (Exception ex){
                                            //Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                                            Log.v("EXECption", ex.getMessage().toString());
                                        }



                                        //Toast.makeText(getApplicationContext(), String.valueOf(places.get(0).getLatLng()), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), Constants.SOMETHING_WENT_WRONG, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            Log.i("TAG", "Clicked: " + item.description);
                            Log.i("TAG", "Called getPlaceById to get Place details for " + item.placeId);
                        }
                    })
            );
        }
        catch (Exception ex){
            Log.d("ERROR", ex.getMessage().toString());
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.v("Google API Callback", "Connection Done");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.v("Google API Callback", "Connection Suspended");
        Log.v("Code", String.valueOf(i));
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.v("Google API Callback","Connection Failed");
        Log.v("Error Code", String.valueOf(connectionResult.getErrorCode()));
        Toast.makeText(this, Constants.API_NOT_CONNECTED,Toast.LENGTH_SHORT).show();
    }

    @Override
         public void onClick(View v) {
        if(v==delete){
            mAutocompleteView.setText("");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mGoogleApiClient.isConnected() && !mGoogleApiClient.isConnecting()){
            Log.v("Google API","Connecting");
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mGoogleApiClient.isConnected()){
            Log.v("Google API","Dis-Connecting");
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
