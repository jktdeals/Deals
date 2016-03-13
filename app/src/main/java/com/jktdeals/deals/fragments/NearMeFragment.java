package com.jktdeals.deals.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.jktdeals.deals.R;
import com.jktdeals.deals.models.DealModel;
import com.jktdeals.deals.parse.ParseInterface;

import java.util.ArrayList;

public class NearMeFragment extends DealsListFragment {
    ArrayList<DealModel> dealsNearMe;
    private ParseInterface pi;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize Parse
        pi = ParseInterface.getInstance(getActivity());
        dealsNearMe = new ArrayList<>();
    }

    // add map to layout
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.frmMap);
        View map =  ((LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.item_map, null, false);
        frameLayout.addView(map);
    }

    // getDeals
    public void getDeals() {
        final ParseInterface.dealLoadNotifier nfy = new ParseInterface.dealLoadNotifier() {
            @Override
            public void notifyLoad(int noOfItems) {
                addAll(dealsNearMe, false);
            }
        };


        pi.getDeals(dealsNearMe, nfy);
    }

}
