package com.jktdeals.deals.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jktdeals.deals.R;
import com.jktdeals.deals.adapters.DealsAdapter;
import com.jktdeals.deals.models.DealModel;

import java.util.ArrayList;
import java.util.List;

public abstract class DealsListFragment extends Fragment {
    RecyclerView rvDeals;
    ArrayList<DealModel> deals;
    DealsAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // create the ArrayList (data source)
        deals = new ArrayList<>();
        // construct the adapter from the data source
        adapter = new DealsAdapter(deals);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_deals_list, container, false);
        rvDeals = (RecyclerView) view.findViewById(R.id.rvDeals);
        rvDeals.setAdapter(adapter);
        // Set layout manager to position the items
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvDeals.setLayoutManager(linearLayoutManager);

        // hook up listener for deal tap to view deal detail
        adapter.setOnItemClickListener(new DealsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                TextView tvDealRestrictions = (TextView) view.findViewById(R.id.tvDealRestrictions);
                if (tvDealRestrictions.getVisibility() == View.GONE) {
                    tvDealRestrictions.setVisibility(View.VISIBLE);
                } else {
                    tvDealRestrictions.setVisibility(View.GONE);
                }
            }
        });

        getDeals();
        return view;
    }

    // Abstract methods to be overridden by fragments extending them
    protected abstract void getDeals();

    public void addAll(List<DealModel> initialOrOlderDeals) {
        final int previousDealsLength = deals.size();
        deals.addAll(initialOrOlderDeals);
        adapter.notifyItemRangeInserted(previousDealsLength, initialOrOlderDeals.size());
    }
}
