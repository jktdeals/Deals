package com.jktdeals.deals.fragments;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jktdeals.deals.R;
import com.jktdeals.deals.helpers.GPSHelper;
import com.jktdeals.deals.models.DealModel;
import com.jktdeals.deals.parse.ParseInterface;

import java.util.ArrayList;

//import permissions.dispatcher.RuntimePermissions;
//
//@RuntimePermissions
public class NearMeFragment extends DealsListFragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {


    ArrayList<DealModel> dealsNearMe;
    ArrayList<Marker> mMarkers;

    private ParseInterface pi;
    private SupportMapFragment mapFragment;
    private GoogleMap map;

    private GPSHelper gpsHelper;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize Parse
        pi = ParseInterface.getInstance(getActivity());
        gpsHelper = new GPSHelper(getActivity());
        dealsNearMe = new ArrayList<>();
    }

    // add map to layout
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        try{
            super.onViewCreated(view, savedInstanceState);
            //FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.frmMap);

//            View map1 =  ((LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.item_map, null, false);
//            frameLayout.addView(map1);

            FragmentManager fm = getChildFragmentManager();
            GoogleMapOptions options = new GoogleMapOptions();

            gpsHelper = new GPSHelper(getContext());
            gpsHelper.getMyLocation();

            double lat = gpsHelper.getLatitude();
            double lon = gpsHelper.getLongitude();

            LatLng latLng = new LatLng(gpsHelper.getLatitude(), gpsHelper.getLongitude());
            // if GPSHelper is getting a null location from the emulator and
            // returning LatLng(0, 0), use Facebook Building 20's coordinates
            if (latLng.equals(new LatLng(0, 0))) {
                latLng = new LatLng(37.4829434, -122.1534673);
            }
            CameraPosition cameraPosition;
            cameraPosition = CameraPosition.fromLatLngZoom(latLng, (float) 14.0);

            options.mapType(GoogleMap.MAP_TYPE_NORMAL).camera(cameraPosition)
                    .zoomControlsEnabled(true).zoomGesturesEnabled(true).ambientEnabled(true);


            mapFragment = SupportMapFragment.newInstance(options);
            //===============resizing===========//
//            ViewGroup.LayoutParams params = mapFragment.getView().getLayoutParams();
//            params.height = 5000;
//            mapFragment.getView().setLayoutParams(params);
            //==================================//
            FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.frmMap);
            frameLayout.setVisibility(View.VISIBLE);
            fm.beginTransaction().replace(R.id.frmMap, mapFragment).commit();

            if(mapFragment != null){
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap map) {
                        loadMap(map);
                    }
                });
            }

        }
        catch (Exception ex){
            Log.v("Exception", ex.getMessage().toString());
        }
    }

    protected void loadMap(GoogleMap googleMap){
        map = googleMap;
        if(map != null){
            Toast.makeText(getContext(), "Map Fragment was loaded properly!", Toast.LENGTH_SHORT).show();

            addMarkersToMap();
        }
    }


    // getDeals
    public void getDeals(final boolean append) {
        final ParseInterface.dealLoadNotifier nfy = new ParseInterface.dealLoadNotifier() {
            @Override
            public void notifyLoad(int noOfItems) {
                addAll(dealsNearMe, append);
            }
        };

        //pi.getDeals(dealsNearMe, nfy);
        LatLng myLatLang = gpsHelper.getLatLng();
        // if GPSHelper is getting a null location from the emulator and
        // returning LatLng(0, 0), use Facebook Building 20's coordinates
        if (myLatLang.equals(new LatLng(0, 0))) {
            myLatLang = new LatLng(37.4829434, -122.1534673);
        }
        pi.getDealsNear(dealsNearMe, nfy, DealsListFragment.DEFULT_PAGE_SIZE, this.current_page, myLatLang);

    }

    private void addMarkersToMap() {

//        try{
//            LatLng ll = new LatLng(gpsHelper.getLatitude(), gpsHelper.getLongitude());
//            BitmapDescriptor defaultMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
//            mMarkers.add(map.addMarker(new MarkerOptions()
//                    .position(ll)
//                    .title("MyStore")
//                    .snippet("123 Main St.")
//                    .icon(defaultMarker)));
//        }
//        catch (Exception ex){
//            Log.v("Exception", ex.getMessage().toString());
//        }


        //map.clear();
        for (int i = 0; i < this.deals.size(); i++) {
            try{
                LatLng latLng =  this.deals.get(i).getLatLang();
                if(latLng != null){
                    BitmapDescriptor defaultMarker = BitmapDescriptorFactory.defaultMarker();

                    if(i%5==0){
                        defaultMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
                    }else if(i%5==1){
                        defaultMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                    }else if(i%5==2){
                        defaultMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
                    }else if(i%5==3){
                        defaultMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
                    }else if(i%5==4){
                        defaultMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE);
                    }

                    mMarkers.add(map.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title(this.deals.get(i).getDealAbstract())
                            .snippet(this.deals.get(i).getStoreName())
                            .icon(defaultMarker)));

                    Log.v(TAG,"Deal "+i+"  was added " +mMarkers.get(mMarkers.size()-1).getId());
                }

            }
            catch (Exception ex){
                Log.v("Exception", ex.getMessage().toString());
            }

        }
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
