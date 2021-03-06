package com.jktdeals.deals.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.jktdeals.deals.R;
import com.jktdeals.deals.activities.CreatDealActivity;
import com.jktdeals.deals.helpers.GPSHelper;
import com.jktdeals.deals.models.DealModel;
import com.jktdeals.deals.parse.ParseInterface;
import com.jktdeals.deals.utility.ExpirationDate;
import com.makeramen.roundedimageview.RoundedImageView;
import com.parse.ParseImageView;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

// based on http://guides.codepath.com/android/Using-the-RecyclerView

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class DealsAdapter extends
        RecyclerView.Adapter<DealsAdapter.ViewHolder> {

    GPSHelper gpsHelper;
    LatLng myLatLang;
    String TAG = "DealsAdapter";
    /*****
     * Creating OnItemClickListener
     *****/

    private Context mContext;

    // Define listener member variable
    private OnItemClickListener listener;
    // Store a member variable for the tweets
    private List<DealModel> mDeals;

    // Pass in the tweet array into the constructor
    public DealsAdapter(List<DealModel> deals) {
        mDeals = deals;
    }

    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public DealsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        this.mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View dealView = inflater.inflate(R.layout.item_deal, parent, false);

        // Initialize GPS Helper
        gpsHelper = new GPSHelper(context.getApplicationContext());

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(dealView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        // Get the data model based on position
        final DealModel deal = mDeals.get(position);
        Log.d(TAG, "onBindViewHolder position: " + position);
        Log.d(TAG, "onBindViewHolder getItemCount: " + mDeals.size());
        final Context context = viewHolder.context;

        // Set item views based on the data model
        TextView tvStoreName = viewHolder.tvStoreName;
        TextView tvDealValue = viewHolder.tvDealValue;
        TextView tvDealDistance = viewHolder.tvDealDistance;
        TextView tvDealName = viewHolder.tvDealName;
        TextView tvDealExpiration = viewHolder.tvDealExpiration;
        TextView tvDealDescription = viewHolder.tvDealDescription;
        TextView tvDealRestrictions = viewHolder.tvDealRestrictions;
        final TextView tvLikes = viewHolder.tvLikes;
        final ImageView ivLikes = viewHolder.ivLikes;
        ImageView ivEdit = viewHolder.ivEdit;
        ImageView ivDelete = viewHolder.ivDelete;
        ImageView ivDealImage = viewHolder.ivDealImage;
        RoundedImageView ivDealCategory = viewHolder.ivDealCategory;
        CardView cvDeal = viewHolder.cvDeal;

        tvStoreName.setText(deal.getStoreName());

        int likesCount = deal.getLikesCount();
        if (likesCount > 0) {
            tvLikes.setText(likesCount + "");
        } else {
            tvLikes.setText("");
        }

        if (deal.isLiked()) {
            ivLikes.setImageResource(R.drawable.ic_favorite_red_18dp);
        } else {
            ivLikes.setImageResource(R.drawable.ic_favorite_outline_white_18dp);
        }

        ivEdit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(mContext, CreatDealActivity.class);
                Bundle bundle = new Bundle();

                //bundle.putParcelable("latLng", deal.getLatLang());
                //bundle.putString("storeName", deal.getStoreName());
                // bundle.putString("value", deal.getDealValue());
                //bundle.putString("abstract", deal.getDealAbstract());
                // bundle.putString("restriction", deal.getDealRestrictions());
                // bundle.putString("description", deal.getDealDescription());
                // bundle.putString("expirationDate", deal.getDealExpiry());

                bundle.putString("id", deal.getObjectId());
                Log.d(TAG, "objid: " + deal.getObjectId());
                //need to add categories.
                intent.putExtras(bundle);
                mContext.startActivity(intent);
                //bundle.putParcelable("DealModel", deal);
            }
        });

        // Setup the click listener for the heart icon
        ivLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deal.isLiked()) {
                    // if it was already liked, unlike it
                    ivLikes.setImageResource(R.drawable.ic_favorite_outline_white_18dp);
                    ParseInterface.getInstance(context).likeDeal(deal, false);

                    int likesCount = deal.getLikesCount();
                    if (likesCount != 0) {
                        tvLikes.setText(likesCount + "");
                    } else {
                        tvLikes.setText("");
                    }
                } else {
                    // if it was previously not liked, like it
                    ivLikes.setImageResource(R.drawable.ic_favorite_red_18dp);
                    ParseInterface.getInstance(context).likeDeal(deal, true);
                    tvLikes.setText(deal.getLikesCount() + "");
                }
            }
        });


        String tempValue = deal.getDealValue();
        if (tempValue.equals("")) {
            tvDealValue.setVisibility(View.INVISIBLE);
        } else {
            tvDealValue.setVisibility(View.VISIBLE);
            tvDealValue.setText("Save " + tempValue);
        }

        ivDealCategory.mutateBackground(true);
        ivDealCategory.setOval(true);
        ivDealCategory.setBackgroundColor(Color.parseColor("#FFFFFF"));
        // set the category icon
        switch(deal.getCATSTRING()) {
            case "Cafe":
                ivDealCategory.setImageResource(R.drawable.coffee);
                break;
            case "Bar":
                ivDealCategory.setImageResource(R.drawable.bar);
                break;
            case "Restaurant":
                ivDealCategory.setImageResource(R.drawable.restaurant);
                break;
            case "Hotel":
                ivDealCategory.setImageResource(R.drawable.hotel);
                break;
            case "Beauty":
                ivDealCategory.setImageResource(R.drawable.beauty);
                break;
            case "Entertainment":
                ivDealCategory.setImageResource(R.drawable.entertainment);
                break;
            case "Pets":
                ivDealCategory.setImageResource(R.drawable.pets);
                break;
            case "Activities":
                ivDealCategory.setImageResource(R.drawable.activities);
                break;
            case "Massage":
                ivDealCategory.setImageResource(R.drawable.massage);
                break;
            case "Apparel":
                ivDealCategory.setImageResource(R.drawable.apparel);
                break;
            case "Groceries":
                ivDealCategory.setImageResource(R.drawable.groceries);
                break;
            case "Local Services":
                ivDealCategory.setImageResource(R.drawable.localservice);
                break;
            case "Home Services":
                ivDealCategory.setImageResource(R.drawable.homeservice);
                break;
            case "Health":
                ivDealCategory.setImageResource(R.drawable.health);
                break;
        };

        tvDealName.setText(deal.getDealAbstract());

        LatLng dealLatLng = deal.getLatLang();
        if (dealLatLng != null) {
            gpsHelper.getMyLocation();
            myLatLang = gpsHelper.getLatLng();

            // if GPSHelper is getting a null location from the emulator and
            // returning LatLng(0, 0), use Facebook Building 20's coordinates
            if (myLatLang.equals(new LatLng(0, 0))) {
                myLatLang = new LatLng(37.4829434, -122.1534673);
            }

            Location myLocation = new Location("myLocation");
            myLocation.setLatitude(myLatLang.latitude);
            myLocation.setLongitude(myLatLang.longitude);
            Location dealLocation = new Location("dealLocation");
            dealLocation.setLatitude(dealLatLng.latitude);
            dealLocation.setLongitude(dealLatLng.longitude);

            // get the "as the crow flies" distance to the deal
            double distance = myLocation.distanceTo(dealLocation);
            // convert from meters to miles, and format
            DecimalFormat myFormatter = new DecimalFormat("#.#");
            String miles = myFormatter.format(distance * 0.00062137119);
            tvDealDistance.setText(miles + "m");
            // set the CardView tag to the distance as well, for an easy
            // way to pass it to Deal Detail when a deal is tapped
            cvDeal.setTag(miles + "m");
        }

        tvDealExpiration.setText("Exp: " + ExpirationDate.formatExpirationDate(deal.getDealExpiry()));

        tvDealDescription.setText(deal.getDealDescription());
        tvDealRestrictions.setText("Restrictions: " + deal.getDealRestrictions());

        if (deal.getDealPic() != null) {
            ParseInterface.populateImageView(ivDealImage, deal.getDealPic());
            //ParseInterface.populateImageView(ivDealImage,new ParseFile(R.mipmap.ic_launcher));
        } else {
            //ivDealImage.setImageResource(R.drawable.placeholder);
            String yelpSnipUrl = deal.getDealYelpSnipUrl();
            if (yelpSnipUrl != null) {
                Picasso.with(ivDealImage.getContext()).load(yelpSnipUrl).fit().centerCrop().into(ivDealImage);
            }
        }

        ParseUser u;
        u = deal.getUser();
        if (!u.isDataAvailable()) {
            try {
                u = u.fetchIfNeeded();
            } catch (com.parse.ParseException e) {
                e.printStackTrace();
            }
        }

        // show the edit and delete icons only for deals the user has created
        if (u.getUsername().equals(ParseUser.getCurrentUser().getUsername())) {
            ivEdit.setVisibility(View.VISIBLE);
            ivDelete.setVisibility(View.VISIBLE);
        }

    }

    // Return the total count of items
    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: " + mDeals.size());
        return mDeals.size();
    }

    // Define the listener interface
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView tvStoreName;
        public TextView tvDealValue;
        public TextView tvDealName;
        public TextView tvDealDistance;
        public TextView tvDealExpiration;
        public TextView tvDealDescription;
        public TextView tvDealRestrictions;
        public TextView tvLikes;
        public ImageView ivLikes;
        public ImageView ivEdit;
        public ImageView ivDelete;
        public RoundedImageView ivDealCategory;
        public ParseImageView ivDealImage;
        public RadioButton tvLikeButton;
        public CardView cvDeal;
        private Context context;

        public ViewHolder(final View itemView) {
            super(itemView);
            this.tvStoreName = (TextView) itemView.findViewById(R.id.tvStoreName);
            this.tvDealValue = (TextView) itemView.findViewById(R.id.tvDealValue);
            this.tvDealName = (TextView) itemView.findViewById(R.id.tvDealName);
            this.tvDealDistance = (TextView) itemView.findViewById(R.id.tvDealDistance);
            this.tvDealExpiration = (TextView) itemView.findViewById(R.id.tvDealExpiration);
            this.tvDealDescription = (TextView) itemView.findViewById(R.id.tvDealDescription);
            this.tvDealRestrictions = (TextView) itemView.findViewById(R.id.tvDealRestrictions);
            this.tvLikes = (TextView) itemView.findViewById(R.id.tvLikes);
            this.ivLikes = (ImageView) itemView.findViewById(R.id.ivLikes);
            this.ivEdit = (ImageView) itemView.findViewById(R.id.ivEdit);
            this.ivDelete = (ImageView) itemView.findViewById(R.id.ivDelete);
            this.ivDealImage = (ParseImageView) itemView.findViewById(R.id.ivDealImage);
            this.ivDealCategory = (RoundedImageView) itemView.findViewById(R.id.ivDealCategory);
            this.cvDeal = (CardView) itemView.findViewById(R.id.cvDeal);

            // set a click listener for when the user clicks the delete icon
            ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Triggers click upwards to the fragment on click
                    if (listener != null)
                        listener.onItemClick(v, getLayoutPosition());
                }
            });

            // Setup the click listener for displaying/hiding the extended item details
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Triggers click upwards to the fragment on click
                    if (listener != null)
                        listener.onItemClick(itemView, getLayoutPosition());
                }
            });
        }
    }

}