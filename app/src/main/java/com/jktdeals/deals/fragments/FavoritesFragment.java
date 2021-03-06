package com.jktdeals.deals.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jktdeals.deals.R;
import com.jktdeals.deals.models.DealModel;
import com.jktdeals.deals.parse.ParseInterface;

import java.util.ArrayList;

public class FavoritesFragment extends DealsListFragment {
    ArrayList<DealModel> dealsFavorites;
    private ParseInterface pi;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize Parse
        pi = ParseInterface.getInstance(getActivity());
    }

    // getDeals
    public void getDeals(final boolean append) {
        final ParseInterface.dealLoadNotifier nfy = new ParseInterface.dealLoadNotifier() {
            @Override
            public void notifyLoad(int noOfItems) {
                TextView tvMyDealsNone = (TextView) getView().findViewById(R.id.tvMyDealsNone);
                if (dealsFavorites.size() == 0) {
                    tvMyDealsNone.setText(R.string.favorite_deals_none);
                    tvMyDealsNone.setVisibility(View.VISIBLE);
                } else {
                    tvMyDealsNone.setVisibility(View.GONE);
                }
                addAll(dealsFavorites, append);
            }
        };

        dealsFavorites = new ArrayList<>();
        pi.getDealsLiked(dealsFavorites, nfy, DealsListFragment.DEFULT_PAGE_SIZE, this.current_page);
        this.current_page++;
    }

    public void deleteDeal(DealModel deal) {
        pi.getInstance(getContext()).deleteDeal(deal);
    }

}
