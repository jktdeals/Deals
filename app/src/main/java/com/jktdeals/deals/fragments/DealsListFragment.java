package com.jktdeals.deals.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jktdeals.deals.R;
import com.jktdeals.deals.adapters.DealsAdapter;
import com.jktdeals.deals.models.DealModel;

import java.util.ArrayList;
import java.util.List;

public abstract class DealsListFragment extends Fragment {
    public static int DEFULT_PAGE_SIZE = 10;
    protected int current_page = 0;
    RecyclerView rvDeals;
    ArrayList<DealModel> deals;
    DealsAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    String TAG = "DealsList";

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
        linearLayoutManager = new LinearLayoutManager(getContext());
        rvDeals.setLayoutManager(linearLayoutManager);

        // hook up listener for deal tap to view deal detail
        adapter.setOnItemClickListener(new DealsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                TextView tvDealDescription = (TextView) view.findViewById(R.id.tvDealDescription);
                TextView tvDealRestrictions = (TextView) view.findViewById(R.id.tvDealRestrictions);
                if (tvDealDescription.getVisibility() == View.GONE) {
                    tvDealDescription.setVisibility(View.VISIBLE);
                    tvDealRestrictions.setVisibility(View.VISIBLE);
                } else {
                    tvDealDescription.setVisibility(View.GONE);
                    tvDealRestrictions.setVisibility(View.GONE);
                }
            }
        });

        // display progress indicator while retrieving deals
        ProgressBar pb = (ProgressBar) view.findViewById(R.id.pbLoading);
        pb.setVisibility(ProgressBar.VISIBLE);
        this.current_page = 0;
        getDeals(false);
        return view;
    }

    // Abstract methods to be overridden by fragments extending them
    protected abstract void getDeals(boolean append);

    public void addAll(List<DealModel> initialOrOlderDeals, boolean append) {
        final int previousDealsLength = deals.size();
        if (append) {
            deals.addAll(initialOrOlderDeals);
            adapter.notifyItemRangeInserted(previousDealsLength, deals.size());


        } else {
            deals.clear();
            deals.addAll(initialOrOlderDeals);
            adapter.notifyDataSetChanged();

        }
        rvDeals.setAdapter(adapter);
        Log.d(TAG, "notified: " + deals.size());

        // get rid of progress indicator after retrieving deals
        ProgressBar pb = (ProgressBar) getView().findViewById(R.id.pbLoading);
        pb.setVisibility(ProgressBar.GONE);
    }

    public void insertNewDeal(DealModel newDeal) {
        deals.add(0, newDeal);
        // notify the adapter
        adapter.notifyItemInserted(0);
        // and scroll up to display the new deal
        linearLayoutManager.scrollToPositionWithOffset(0, 0);
    }

}
