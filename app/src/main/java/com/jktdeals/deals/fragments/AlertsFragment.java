package com.jktdeals.deals.fragments;

import android.os.Bundle;

import com.jktdeals.deals.models.DealModel;
import com.jktdeals.deals.parse.ParseInterface;

import java.util.ArrayList;

public class AlertsFragment extends DealsListFragment {
    ArrayList<DealModel> dealsAlerts;
    private ParseInterface pi;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize Parse
        pi = ParseInterface.getInstance(getActivity());
    }

    @Override
    // getDeals
    public void getDeals(final boolean append) {
        final ParseInterface.dealLoadNotifier nfy = new ParseInterface.dealLoadNotifier() {
            @Override
            public void notifyLoad(int noOfItems) {
                addAll(dealsAlerts, append);
            }
        };

        dealsAlerts = new ArrayList<>();
        pi.getDealsPaged(dealsAlerts, nfy, DealsListFragment.DEFULT_PAGE_SIZE, this.current_page);

    }

    public void deleteDeal(DealModel deal) {
        pi.getInstance(getContext()).deleteDeal(deal);
    }

}
