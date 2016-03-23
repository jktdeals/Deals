package com.jktdeals.deals.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListPopupWindow;

import com.google.android.gms.maps.model.LatLng;
import com.jktdeals.deals.R;
import com.jktdeals.deals.activities.DealsActivity;
import com.jktdeals.deals.adapters.DealNotificationAdapter;
import com.jktdeals.deals.helpers.GPSHelper;
import com.jktdeals.deals.models.DealModel;
import com.jktdeals.deals.parse.ParseInterface;

import java.util.ArrayList;

/**
 * Created by josevillanuva on 3/19/16.
 */
public class NotificationService extends Service {
    private WindowManager windowManager;
    private ImageView chatHead;

    public static  int ID_NOTIFICATION = 2018;
    public static final String TAG = "NotificationServiceTag";
    boolean mHasDoubleClicked = false;
    long lastPressTime;

    private float mDownX;
    private float mDownY;
    private boolean isOnClick;
    private final float SCROLL_THRESHOLD = 10;

    private ArrayList<DealModel> myNewDeals;
    private DealNotificationAdapter dealNotificationAdapter;

    private ParseInterface pi;
    private GPSHelper gpsHelper;

    @Override public IBinder onBind(Intent intent) {
        // Not used
        return null;
    }


    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        Bundle extras = intent.getExtras();
        int newDealsCount = extras.getInt("newDealsCount");
        if(extras != null){
            for(int i = 0; i < newDealsCount; i++){
                String nameField = "deal"+i;
                String dealAbstract = extras.getString(nameField+"Abstract");
                String dealValue = extras.getString(nameField+"Value");
                String dealDescription = extras.getString(nameField+"Description");
                String dealCathegory = extras.getString(nameField+"Category");

                DealModel newDeal = pi.createDealObject(dealValue, dealAbstract, dealDescription,
                        "", "", new LatLng(0,0), "", "", "", "", "" );
                newDeal.setCATSTRING(dealCathegory);
                this.myNewDeals.add(newDeal);
            }
        }
    }

    @Override public void onCreate() {
        super.onCreate();
        pi = ParseInterface.getInstance(this);
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        chatHead = new ImageView(this);

        //createSampleDeals();

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 100;
        myNewDeals = new ArrayList<DealModel>();
        dealNotificationAdapter = new DealNotificationAdapter(getApplicationContext(), R.layout.item_deal_notification, myNewDeals);

        windowManager.addView(chatHead, params);

        try{
            chatHead.setOnTouchListener(new View.OnTouchListener() {
                private WindowManager.LayoutParams paramsF = params;
                private int initialX;
                private int initialY;
                private float initialTouchX;
                private float initialTouchY;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:

                            long pressTime = System.currentTimeMillis();
                            if (pressTime - lastPressTime <= 300) {
                                //createNotification();
                                //myNewDeals.clear();

                                if(dealNotificationAdapter != null){
                                    dealNotificationAdapter.clear();
                                }

                                NotificationService.this.stopSelf();
                                mHasDoubleClicked = true;
                            } else {     // If not double click....
                                mHasDoubleClicked = false;
                            }

                            mDownX = event.getX();
                            mDownY = event.getY();
                            isOnClick = true;
                            lastPressTime = pressTime;

                            initialX = paramsF.x;
                            initialY = paramsF.y;
                            initialTouchX = event.getRawX();
                            initialTouchY = event.getRawY();
                            break;

                        case MotionEvent.ACTION_UP:
                            if (isOnClick) {
                                onClick();
                            } else if (mHasDoubleClicked) {
                            }

                            break;

                        case MotionEvent.ACTION_MOVE:
                            paramsF.x = initialX + (int) (event.getRawX() - initialTouchX);
                            paramsF.y = initialY + (int) (event.getRawY() - initialTouchY);
                            windowManager.updateViewLayout(chatHead, paramsF);
                            if (isOnClick && (Math.abs(mDownX - event.getX()) > SCROLL_THRESHOLD || Math.abs(mDownY - event.getY()) > SCROLL_THRESHOLD)) {
                                Log.i("ChatHead", "movement detected");
                                isOnClick = false;
                            }

                            isOnClick = false;

                            break;
                    }
                    return false;
                }
            });
        } catch (Exception ex) {
          Log.v("ChatHead", ex.getMessage());
        }

        chatHead.setImageResource(R.drawable.ic_circle_large);
    }

    private void onClick() {
        try {
            initiatePopupWindow(chatHead);

        } catch (Exception ex) {
            Log.v("ChatHeadSevice", ex.getMessage());
        }
    }


    private void initiatePopupWindow(View anchor) {
        try {
            Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            ListPopupWindow popup = new ListPopupWindow(this);
            popup.setAnchorView(anchor);
            popup.setWidth((int) (display.getWidth() / (2.0)));
            //dealNotificationAdapter = new DealNotificationAdapter(getApplicationContext(), R.layout.item_deal_notification, myNewDeals);
            popup.setAdapter(dealNotificationAdapter);
            //popup.setAdapter(new DealNotificationAdapter(getApplicationContext(), R.layout.item_deal_notification, myNewDeals));
            popup.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View view, int position, long id3) {
                    Bundle bundle = new Bundle();
                    bundle.putString("startMethod", "Service");

                    Intent intent = new Intent(NotificationService.this, DealsActivity.class);
                    intent.putExtras(bundle);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);

                    //myNewDeals.clear();
                }
            });

            popup.show();

        } catch (Exception e) {
            e.printStackTrace();
            Log.v("Chathead", e.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        try{super.onDestroy();
            //Toast.makeText(this, "Removing..", Toast.LENGTH_SHORT).show();
            if (chatHead != null)
            {
//                if(dealNotificationAdapter != null){
//                    dealNotificationAdapter.clear();
//                }
                //dealNotificationAdapter.clear();
                windowManager.removeView(chatHead);
            }}
        catch (Exception ex){
            Log.v("ChatHeadClosing", ex.getMessage());
        }

    }
}
