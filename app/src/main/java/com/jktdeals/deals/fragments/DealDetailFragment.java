package com.jktdeals.deals.fragments;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jktdeals.deals.R;
import com.jktdeals.deals.activities.DealsActivity;
import com.jktdeals.deals.models.DealModel;
import com.jktdeals.deals.utility.Constants;
import com.jktdeals.deals.utility.ExpirationDate;
import com.parse.ParseFile;
import com.squareup.picasso.Picasso;

public class DealDetailFragment extends android.support.v4.app.DialogFragment {

    public DealDetailFragment() {
        // Required empty public constructor
    }

    public static DealDetailFragment newInstance(DealModel deal, int position, String distance) {
        DealDetailFragment dealDetailFragment = new DealDetailFragment();
        Bundle args = new Bundle();
        ParseFile dealPhoto = deal.getDealPic();
        if (dealPhoto != null) {
            String photoUrl = dealPhoto.getUrl();
            Uri imageUri = Uri.parse(photoUrl);
            args.putString("dealImageUrl", imageUri.toString());
        } else {
            args.putString("dealImageUrl", deal.getDealYelpSnipUrl());
        }

        args.putString("dealValue", deal.getDealValue());
        args.putString("storeName", deal.getStoreName());
        args.putString("storeDistance", distance);

        // I have the store phone number and address in the STORE_DESCRIPTION
        // field as a bar-delimited string
        String[] storePhoneAndAddress = deal.getStoreDescription().split("\\|");
        args.putString("storePhoneNumber", storePhoneAndAddress[0]);
        if (storePhoneAndAddress.length > 1) {
            args.putString("storeAddress", storePhoneAndAddress[1]);
        } else {
            args.putString("storeAddress", null);
        }

        args.putString("storeWebSite", deal.getStoreAbstract());
        args.putString("dealName", deal.getDealAbstract());
        args.putString("dealDescription", deal.getDealDescription());
        args.putString("dealRestrictions", deal.getDealRestrictions());
        args.putString("dealPostedBy", deal.getUser().getUsername());
        args.putString("dealExpirationDate", deal.getDealExpiry());
        args.putString("dealYelpUrl", deal.getDealYelpMobileUrl());
        args.putString("dealYelpRatingImageUrl", deal.getDealYelpRatingImageUrl());
        args.putString("dealCategory", deal.getCATSTRING());
        args.putInt("position", position);
        dealDetailFragment.setArguments(args);
        return dealDetailFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_deal_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        // Get fields from view
        ImageView ivDealImage = (ImageView) view.findViewById(R.id.ivDealImage);
        ImageView ivClose = (ImageView) view.findViewById(R.id.ivClose);
        ImageView ivCategory = (ImageView) view.findViewById(R.id.ivCategory);
        TextView tvDealValue = (TextView) view.findViewById(R.id.tvDealValue);
        TextView tvStoreName = (TextView) view.findViewById(R.id.tvStoreName);
        TextView tvStoreAddress = (TextView) view.findViewById(R.id.tvStoreAddress);
        TextView tvStoreDistance = (TextView) view.findViewById(R.id.tvStoreDistance);
        TextView tvDealName = (TextView) view.findViewById(R.id.tvDealName);
        TextView tvDescription = (TextView) view.findViewById(R.id.tvDealDescription);
        TextView tvRestrictions = (TextView) view.findViewById(R.id.tvDealRestrictions);
        TextView tvDealPostedBy = (TextView) view.findViewById(R.id.tvDealPostedBy);
        TextView tvDealExpiration = (TextView) view.findViewById(R.id.tvDealExpiration);
        ImageView ivYelpLogo = (ImageView) view.findViewById(R.id.ivYelpLogo);
        ImageView ivYelpRating = (ImageView) view.findViewById(R.id.ivYelpRating);
        Button btnCall = (Button) view.findViewById(R.id.btnCall);
        ImageView ivCall = (ImageView) view.findViewById(R.id.ivCall);
        Button btnMap = (Button) view.findViewById(R.id.btnMap);
        ImageView ivMap = (ImageView) view.findViewById(R.id.ivMap);
        Button btnWeb = (Button) view.findViewById(R.id.btnWeb);
        ImageView ivWeb = (ImageView) view.findViewById(R.id.ivWeb);

        // get values from bundle

        // I'm doing the image like this because the Parsefiles aren't Serializable
        // or Parcelable in a bundle
        String uriString = getArguments().getString("dealImageUrl");
        if (uriString != null) {
            Picasso.with(getContext()).load(uriString).fit().centerCrop().into(ivDealImage);
        }

        String tempValue = getArguments().getString("dealValue");
        if (tempValue.equals("")) {
            tvDealValue.setVisibility(View.INVISIBLE);
        } else {
            tvDealValue.setVisibility(View.VISIBLE);
            tvDealValue.setText("Save " + tempValue);
        }

        tempValue = getArguments().getString("storeAddress");
        if (tempValue == null) {
            tvStoreAddress.setVisibility(View.GONE);
        } else {
            // the store address is comma-delimited and even includes state, zip code,
            // and country -- just display the first two parts
            String[] parts = tempValue.split(",\\s");
            tvStoreAddress.setVisibility(View.VISIBLE);
            tvStoreAddress.setText(parts[0] + ", " + parts[1]);
        }

        tvStoreName.setText(getArguments().getString("storeName"));
        tvStoreDistance.setText(getArguments().getString("storeDistance"));

        // set the category icon
        switch(getArguments().getString("dealCategory")) {
            case "Cafe":
                ivCategory.setImageResource(R.drawable.coffee);
                break;
            case "Bar":
                ivCategory.setImageResource(R.drawable.bar);
                break;
            case "Restaurant":
                ivCategory.setImageResource(R.drawable.restaurant);
                break;
            case "Hotel":
                ivCategory.setImageResource(R.drawable.hotel);
                break;
            case "Beauty":
                ivCategory.setImageResource(R.drawable.beauty);
                break;
            case "Entertainment":
                ivCategory.setImageResource(R.drawable.entertainment);
                break;
            case "Pets":
                ivCategory.setImageResource(R.drawable.pets);
                break;
            case "Activities":
                ivCategory.setImageResource(R.drawable.activities);
                break;
            case "Massage":
                ivCategory.setImageResource(R.drawable.massage);
                break;
            case "Apparel":
                ivCategory.setImageResource(R.drawable.apparel);
                break;
            case "Groceries":
                ivCategory.setImageResource(R.drawable.groceries);
                break;
            case "Local Services":
                ivCategory.setImageResource(R.drawable.localservice);
                break;
            case "Home Services":
                ivCategory.setImageResource(R.drawable.homeservice);
                break;
            case "Health":
                ivCategory.setImageResource(R.drawable.health);
                break;
        };

        tvDealName.setText(getArguments().getString("dealName"));
        tvDescription.setText(getArguments().getString("dealDescription"));

        String restrictions = getArguments().getString("dealRestrictions");
        if (restrictions.equals("")) {
            tvRestrictions.setVisibility(View.GONE);
        } else {
            tvRestrictions.setText("Restrictions: " + restrictions);
        }
        tvDealPostedBy.setText("Posted by " + getArguments().getString("dealPostedBy"));
        tvDealExpiration.setText("Exp: " + ExpirationDate.formatExpirationDate(getArguments().getString("dealExpirationDate")));

        Picasso.with(getContext()).load(getArguments().getString("dealYelpRatingImageUrl")).into(ivYelpRating);

        // the X in the top-left corner closes the fragment
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        // clicking either the Yelp logo or rating brings up the Yelp listing for the store
        ivYelpLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(getArguments().getString("dealYelpUrl"))));
            }
        });
        ivYelpRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(getArguments().getString("dealYelpUrl"))));
            }
        });

        // Store phone
        ivCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callStorePhoneNumberIfWeHaveOrCanGetPermission();
            }
        });
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callStorePhoneNumberIfWeHaveOrCanGetPermission();
            }
        });

        // Store oon the map
        ivMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                focusMapOnDeal();
            }
        });
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                focusMapOnDeal();
            }
        });

        // Store website
        ivWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToStoreWebsite();
            }
        });
        btnWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToStoreWebsite();
            }
        });
    }

    private void callStorePhoneNumberIfWeHaveOrCanGetPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CALL_PHONE);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            // if we already have permission, go ahead and call
            callStorePhoneNumber();
        } else {
            // if we don't have permission, and it's an Android M phone,
            // request permission
            requestPermissions(new String[] { Manifest.permission.CALL_PHONE },
                    Constants.MY_PERMISSIONS_REQUEST_CALL_PHONE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            // the callback from asking permission on an Android M phone
            case Constants.MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, go ahead and call
                    callStorePhoneNumber();
                }
                return;
            }
        }
    }

    private void callStorePhoneNumber() {
        // once we have permission, go ahead and make the call
        String storePhone = getArguments().getString("storePhoneNumber");
        if (storePhone != null) {
            try {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + storePhone));
                startActivity(callIntent);
            } catch (ActivityNotFoundException activityException) {
                Log.e("Calling a Phone Number", "Call failed", activityException);
            }
        }
    }

    private void focusMapOnDeal() {
        // find out which tab/fragment is active, and tell it which deal to focus the map on
        int whichDeal = getArguments().getInt("position");
        DealsActivity dealsActivity = (DealsActivity) getActivity();
        switch(dealsActivity.viewPager.getCurrentItem()) {
            case 0:
                NearMeFragment nearMeFragment = (NearMeFragment) dealsActivity.dealsFragmentPagerAdapter.getRegisteredFragment(0);
                nearMeFragment.focusMapOnDeal(whichDeal);
                break;
            case 1:
                MyDealsFragment myDealsFragment = (MyDealsFragment) dealsActivity.dealsFragmentPagerAdapter.getRegisteredFragment(1);
                myDealsFragment.focusMapOnDeal(whichDeal);
                break;
            case 2:
                FavoritesFragment favoritesFragment = (FavoritesFragment) dealsActivity.dealsFragmentPagerAdapter.getRegisteredFragment(2);
                favoritesFragment.focusMapOnDeal(whichDeal);
                break;
            case 3:
                AlertsFragment alertsFragment = (AlertsFragment) dealsActivity.dealsFragmentPagerAdapter.getRegisteredFragment(3);
                alertsFragment.focusMapOnDeal(whichDeal);
                break;
        }
        // and close the Deal Detail fragment
        getDialog().dismiss();
    }

    private void goToStoreWebsite() {
        String storeWeb = getArguments().getString("storeWebSite");
        if (storeWeb != null) {
            startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(storeWeb)));
        }
    }

}
