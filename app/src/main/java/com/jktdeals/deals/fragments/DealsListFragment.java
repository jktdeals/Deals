package com.jktdeals.deals.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.jktdeals.deals.R;
import com.jktdeals.deals.activities.DealsActivity;
import com.jktdeals.deals.adapters.DealsAdapter;
import com.jktdeals.deals.models.DealModel;

import java.util.ArrayList;
import java.util.List;

public abstract class DealsListFragment extends Fragment {
    public static int DEFULT_PAGE_SIZE = 20;
    protected int current_page = 0;
    RecyclerView rvDeals;
    ArrayList<DealModel> deals;
    DealsAdapter adapter;
    GridLayoutManager gridLayoutManager;
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
        // Define 2 column grid layout
        gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        // Set layout manager to position the items
        rvDeals.setLayoutManager(gridLayoutManager);

        // hook up listener for tap delete icon to delete,
        // and also deal tap to bring up deal detail fragment
        adapter.setOnItemClickListener(new DealsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                // get the deal
                final DealModel deal = (DealModel) deals.get(position);
                // if the delete icon was clicked
                if (view.getTag().equals("Delete")) {
                    // diaplay an "Are you sure you want to delete ... " dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle(R.string.delete_deal);
                    builder.setMessage("Are you sure you want to delete your deal \"" + deal.getDealAbstract() + "\"?");
                    builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // they confirmed, delete the deal
                            DealsActivity dealsActivity = (DealsActivity) getActivity();
                            // remove it from the ArrayList
                            deals.remove(position);
                            // notify the adapter
                            adapter.notifyItemRemoved(position);
                            // and find the current tab and its fragment and have it have Parse delete the deal
                            switch(dealsActivity.viewPager.getCurrentItem()) {
                                case 0:
                                    NearMeFragment nearMeFragment = (NearMeFragment) dealsActivity.dealsFragmentPagerAdapter.getRegisteredFragment(0);
                                    nearMeFragment.deleteDeal(deal);
                                    break;
                                case 1:
                                    MyDealsFragment myDealsFragment = (MyDealsFragment) dealsActivity.dealsFragmentPagerAdapter.getRegisteredFragment(1);
                                    myDealsFragment.deleteDeal(deal);
                                    break;
                                case 2:
                                    FavoritesFragment favoritesFragment = (FavoritesFragment) dealsActivity.dealsFragmentPagerAdapter.getRegisteredFragment(2);
                                    favoritesFragment.deleteDeal(deal);
                                    break;
                                case 3:
                                    AlertsFragment alertsFragment = (AlertsFragment) dealsActivity.dealsFragmentPagerAdapter.getRegisteredFragment(3);
                                    alertsFragment.deleteDeal(deal);
                                    break;
                            }
                        }
                    });
                    builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // they canceled, don't do anything
                        }
                    });
                    builder.show();
                } else {
                    // if they tapped the deal aside from the edit or delete icons,
                    // bring up the deal detail fragment
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    DealDetailFragment dealDetailFragment = DealDetailFragment.newInstance(deal, position, view.getTag().toString());
                    dealDetailFragment.show(fm, "fragment_deal_detail");
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
        gridLayoutManager.scrollToPositionWithOffset(0, 0);
    }

    public void focusMapOnDeal(int position) {
        // the user tapped Map on the Deal Detail fragment
        DealsActivity dealsActivity = (DealsActivity) getActivity();
        dealsActivity.focusMapOnDeal(deals.get(position));
    }
}
