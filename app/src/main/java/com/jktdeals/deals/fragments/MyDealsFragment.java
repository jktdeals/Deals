package com.jktdeals.deals.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jktdeals.deals.R;
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
    public void getDeals(final boolean append) {
        final ParseInterface.dealLoadNotifier nfy = new ParseInterface.dealLoadNotifier() {
            @Override
            public void notifyLoad(int noOfItems) {
                TextView tvMyDealsNone = (TextView) getView().findViewById(R.id.tvMyDealsNone);
                if (dealsMy.size() == 0) {
                    tvMyDealsNone.setVisibility(View.VISIBLE);
                } else {
                    tvMyDealsNone.setVisibility(View.GONE);
                }
                addAll(dealsMy, append);
            }
        };

        dealsMy = new ArrayList<>();
        pi.getMyDeals(dealsMy, nfy);
    }

}