package com.jktdeals.deals.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.jktdeals.deals.R;
import com.jktdeals.deals.activities.CreatDealActivity;
import com.jktdeals.deals.models.DealModel;
import com.jktdeals.deals.parse.ParseInterface;
import com.parse.ParseImageView;
import com.parse.ParseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// based on http://guides.codepath.com/android/Using-the-RecyclerView

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class DealsAdapter extends
        RecyclerView.Adapter<DealsAdapter.ViewHolder> {

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
        TextView tvDealName = viewHolder.tvDealName;
        TextView tvDealExpiration = viewHolder.tvDealExpiration;
        TextView tvDealDescription = viewHolder.tvDealDescription;
        TextView tvDealRestrictions = viewHolder.tvDealRestrictions;
        final TextView tvLikes = viewHolder.tvLikes;
        final ImageView ivLikes = viewHolder.ivLikes;
        ImageView ivEdit = viewHolder.ivEdit;
        ImageView ivDelete = viewHolder.ivDelete;
        ImageView tvDealImage = viewHolder.tvDealImage;

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
            ivLikes.setImageResource(R.drawable.ic_favorite_outline_grey600_18dp);
        }

        ivEdit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(mContext, CreatDealActivity.class);
                Bundle bundle = new Bundle();

                bundle.putParcelable("latLng", deal.getLatLang());
                bundle.putString("storeName", deal.getStoreName());
                bundle.putString("value", deal.getDealValue());
                bundle.putString("abstract", deal.getDealAbstract());
                bundle.putString("restriction", deal.getDealRestrictions());
                bundle.putString("description", deal.getDealDescription());
                bundle.putString("expirationDate", deal.getDealExpiry());
                //need to add categories.
                intent.putExtras(bundle);
                mContext.startActivity(intent);
                //bundle.putParcelable("DealModel", deal);
            }
        });

        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDeals.remove(position);
                ParseInterface.getInstance(context).deleteDeal(deal);
                notifyItemChanged(position);
            }
        });

        // Setup the click listener for the heart icon
        ivLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deal.isLiked()) {

                    ivLikes.setImageResource(R.drawable.ic_favorite_outline_grey600_18dp);
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
//        if (tempValue == null) {
//            tvDealValue.setText("null");
//        } else {
        tvDealValue.setText(tempValue + " off");


//        }
        tvDealName.setText(deal.getDealAbstract());
        String tempDate = deal.getDealExpiry();
        Pattern p = Pattern.compile("^\\d\\d/\\d\\d/\\d\\d$");
        Matcher m = p.matcher(tempDate);
        if (m.matches()) {
            // if the expiration date uses the yy/mm/dd format from Jose's
            // CreatDealActivity, parse it
            Date date;
            long dateMillis = 0;
            date = new Date(dateMillis);
            String yymmddFormat = "yy/MM/dd";
            SimpleDateFormat sf = new SimpleDateFormat(yymmddFormat, Locale.ENGLISH);
            sf.setLenient(true);

            try {
                dateMillis = sf.parse(tempDate).getTime();
                date = new Date(dateMillis);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            String moreFriendlyDateFormat = "MMM d, yyyy";
            SimpleDateFormat mfdf = new SimpleDateFormat(moreFriendlyDateFormat, Locale.ENGLISH);
            mfdf.setLenient(true);

            tvDealExpiration.setText("Expires: " + mfdf.format(date));
        } else {
            // otherwise just put whatever value is there
            tvDealExpiration.setText("Expires: " + tempDate);
        }
        tvDealDescription.setText(deal.getDealDescription());
        tvDealRestrictions.setText("Restrictions: " + deal.getDealRestrictions());

        if (deal.getDealPic() != null) {
            ParseInterface.populateImageView(tvDealImage, deal.getDealPic());
            //ParseInterface.populateImageView(tvDealImage,new ParseFile(R.mipmap.ic_launcher));
        } else {
            tvDealImage.setImageResource(R.mipmap.ic_launcher);

        }

        // show the edit and delete icons only for deals the user has created
        ParseUser u;
        u = deal.getUser();
        if (!u.isDataAvailable()) {
            try {
                u = u.fetchIfNeeded();
            } catch (com.parse.ParseException e) {
                e.printStackTrace();
            }
        }

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
        public TextView tvDealExpiration;
        public TextView tvDealDescription;
        public TextView tvDealRestrictions;
        public TextView tvLikes;
        public ImageView ivLikes;
        public ImageView ivEdit;
        public ImageView ivDelete;
        public ParseImageView tvDealImage;
        public RadioButton tvLikeButton;
        private Context context;

        public ViewHolder(final View itemView) {
            super(itemView);
            this.tvStoreName = (TextView) itemView.findViewById(R.id.tvStoreName);
            this.tvDealValue = (TextView) itemView.findViewById(R.id.tvDealValue);
            this.tvDealName = (TextView) itemView.findViewById(R.id.tvDealName);
            this.tvDealExpiration = (TextView) itemView.findViewById(R.id.tvDealExpiration);
            this.tvDealDescription = (TextView) itemView.findViewById(R.id.tvDealDescription);
            this.tvDealRestrictions = (TextView) itemView.findViewById(R.id.tvDealRestrictions);
            this.tvLikes = (TextView) itemView.findViewById(R.id.tvLikes);
            this.ivLikes = (ImageView) itemView.findViewById(R.id.ivLikes);
            this.ivEdit = (ImageView) itemView.findViewById(R.id.ivEdit);
            this.ivDelete = (ImageView) itemView.findViewById(R.id.ivDelete);
            this.tvDealImage = (ParseImageView) itemView.findViewById(R.id.ivDealImage);
            // Setup the click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Triggers click upwards to the adapter on click
                    if (listener != null)
                        listener.onItemClick(itemView, getLayoutPosition());
                }
            });
        }
    }

}