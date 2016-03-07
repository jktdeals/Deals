package com.jktdeals.deals.fragments;

import android.os.Bundle;

import com.jktdeals.deals.models.DealModel;
import com.jktdeals.deals.parse.ParseInterface;

import java.util.ArrayList;

public class AlertsFragment extends DealsListFragment {
    private ParseInterface pi;
    ArrayList<DealModel> dealsAlerts;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize Parse
        pi = ParseInterface.getInstance(getActivity());
    }

    @Override
    // getDeals
    public void getDeals() {
        final ParseInterface.dealLoadNotifier nfy = new ParseInterface.dealLoadNotifier() {
            @Override
            public void notifyLoad(int noOfItems) {
                addAll(dealsAlerts);
            }
        };

        dealsAlerts = new ArrayList<>();
        pi.getDeals(dealsAlerts, nfy);
    }

}
