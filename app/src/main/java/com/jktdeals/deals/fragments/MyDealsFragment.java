package com.jktdeals.deals.fragments;

import android.os.Bundle;

import com.jktdeals.deals.models.DealModel;
import com.jktdeals.deals.parse.ParseInterface;

import java.util.ArrayList;

public class MyDealsFragment extends DealsListFragment {
    ArrayList<DealModel> dealsMy;
    private ParseInterface pi;

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
                addAll(dealsMy, false);
            }
        };

        dealsMy = new ArrayList<>();
        pi.getMyDeals(dealsMy, nfy);
    }

}