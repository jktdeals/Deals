package com.jktdeals.deals.fragments;

import android.os.Bundle;

import com.jktdeals.deals.models.DealModel;
import com.jktdeals.deals.parse.ParseInterface;

import java.util.ArrayList;

public class FavoritesFragment extends DealsListFragment {
    private ParseInterface pi;
    ArrayList<DealModel> dealsFavorites;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize Parse
        pi = ParseInterface.getInstance(getActivity());
    }

    // getDeals
    public void getDeals() {
        final ParseInterface.dealLoadNotifier nfy = new ParseInterface.dealLoadNotifier() {
            @Override
            public void notifyLoad(int noOfItems) {
                addAll(dealsFavorites);
            }
        };

        dealsFavorites = new ArrayList<>();
        pi.getDeals(dealsFavorites, nfy);
    }

}
