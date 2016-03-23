package com.jktdeals.deals.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.jktdeals.deals.R;
import com.jktdeals.deals.models.DealModel;
import com.jktdeals.deals.utility.ExpirationDate;
import com.parse.ParseFile;
import com.squareup.picasso.Picasso;

public class DealDetailFragment extends android.support.v4.app.DialogFragment {

    public DealDetailFragment() {
        // Required empty public constructor
    }

    public static DealDetailFragment newInstance(DealModel deal) {
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
        args.putString("dealName", deal.getDealAbstract());
        args.putString("dealDescription", deal.getDealDescription());
        args.putString("dealRestrictions", deal.getDealRestrictions());
        args.putString("dealPostedBy", deal.getUser().getUsername());
        args.putString("dealExpirationDate", deal.getDealExpiry());
        args.putString("dealYelpUrl", deal.getDealYelpMobileUrl());
        args.putString("dealYelpRatingImageUrl", deal.getDealYelpRatingImageUrl());
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
        TextView tvDealValue = (TextView) view.findViewById(R.id.tvDealValue);
        TextView tvStoreName = (TextView) view.findViewById(R.id.tvStoreName);
        TextView tvDealName = (TextView) view.findViewById(R.id.tvDealName);
        TextView tvDescription = (TextView) view.findViewById(R.id.tvDealDescription);
        TextView tvRestrictions = (TextView) view.findViewById(R.id.tvDealRestrictions);
        TextView tvDealPostedBy = (TextView) view.findViewById(R.id.tvDealPostedBy);
        TextView tvDealExpiration = (TextView) view.findViewById(R.id.tvDealExpiration);
        ImageView ivYelpLogo = (ImageView) view.findViewById(R.id.ivYelpLogo);
        ImageView ivYelpRating = (ImageView) view.findViewById(R.id.ivYelpRating);

        // get values from bundle

        // I'm doing the image like this because the Parsefiles aren't Serializable
        // or Parcelable in a bundle
        String uriString = getArguments().getString("dealImageUrl");
        if (uriString != null) {
            Picasso.with(getContext()).load(uriString).fit().centerCrop().into(ivDealImage);
        }

        tvDealValue.setText(getArguments().getString("dealValue") + " off");
        tvStoreName.setText(getArguments().getString("storeName"));
        tvDealName.setText(getArguments().getString("dealName"));
        tvDescription.setText(getArguments().getString("dealDescription"));

        String restrictions = getArguments().getString("dealRestrictions");
        if (restrictions.equals("")) {
            tvRestrictions.setVisibility(View.GONE);
        } else {
            tvRestrictions.setText("Restrictions: " + restrictions);
        }
        tvDealPostedBy.setText("Deal posted by " + getArguments().getString("dealPostedBy"));
        tvDealExpiration.setText("Expiration date: " + ExpirationDate.formatExpirationDate(getArguments().getString("dealExpirationDate")));

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

    }

}
