package com.jktdeals.deals;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.jktdeals.deals.activities.DealsActivity;
import com.jktdeals.deals.models.DealModel;
import com.jktdeals.deals.parse.ParseInterface;
import com.jktdeals.deals.services.NotificationService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class MyCustomReceiver extends BroadcastReceiver {
    public static final String intentAction = "com.parse.push.intent.RECEIVE";
    public static final int NOTIFICATION_ID = 45;
    private static final String TAG = "MyCustomReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            Log.d(TAG, "Receiver intent null");
        } else {
            // Parse push message and handle accordingly
            processPush(context, intent);
        }
    }

    private void processPush(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "got action " + action);
        if (action.equals(intentAction)) {
            String channel = intent.getExtras().getString("com.parse.Channel");
            try {
                JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
                Log.d(TAG, json.toString());
                Log.d(TAG, "got action " + action + " on channel " + channel + " with:");
                // Iterate the parse keys if needed
                Iterator<String> itr = json.keys();
                while (itr.hasNext()) {
                    String key = itr.next();
                    String value = json.getString(key);
                    Log.d(TAG, "..." + key + " => " + value);
                    // Extract custom push data
                    if (key.equals("customdata")) {
                        // create a local notification

                        createNotification(context, value);
                    } else if (key.equals("launch")) {
                        // Handle push notification by invoking activity directly
                        launchSomeActivity(context, value);
                    } else if (key.equals("broadcast")) {
                        // OR trigger a broadcast to activity
                        triggerBroadcastToActivity(context, value);
                    }
                }
            } catch (JSONException ex) {
                Log.d(TAG, "JSON failed!");
            }
        }
    }

    // Create a local dashboard notification to tell user about the event
    // See: http://guides.codepath.com/android/Notifications
    private void createNotification(Context context, String datavalue) {

        DealModel dealO = ParseInterface.getInstance(context).lookupById(datavalue);
        String notificationText = "New Deal Added!";
        if (dealO != null) {
            // No Notification for creator
            //if (!dealO.getUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId()))
            {
                ArrayList<DealModel> newDeals = new ArrayList<>();
                newDeals.add(dealO);
                createServiceNotification(context, newDeals);

            }

        }

        //TODO: remove below code after Jose displays the deals viachat head
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context).setSmallIcon(
//                R.drawable.ic_favorite_red_18dp).setContentTitle("Deal Notification: " + datavalue).setContentText(notificationText);
//        NotificationManager mNotificationManager = (NotificationManager) context
//                .getSystemService(Context.NOTIFICATION_SERVICE);
//        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }


    // Handle push notification by invoking activity directly
    // See: http://guides.codepath.com/android/Using-Intents-to-Create-Flows
    private void launchSomeActivity(Context context, String datavalue) {
        Intent pupInt = new Intent(context, DealsActivity.class);
        pupInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        pupInt.putExtra("data", datavalue);
        context.getApplicationContext().startActivity(pupInt);
    }

    // Handle push notification by sending a local broadcast
    // to which the activity subscribes to
    // See: http://guides.codepath.com/android/Starting-Background-Services#communicating-with-a-broadcastreceiver
    private void triggerBroadcastToActivity(Context context, String datavalue) {
        Intent intent = new Intent(intentAction);
        intent.putExtra("data", datavalue);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public void createServiceNotification(Context context, ArrayList<DealModel> newDealList) {
        //TODO: To Jose to handle
        if(!isMyServiceRunning(context, NotificationService.class)){

            Bundle bundle = new Bundle();
            int newDealsCount = 0;
            for (int i = 0; i < newDealList.size(); i++) {
                String nameField = "deal" + i;
                bundle.putString(nameField + "Abstract", newDealList.get(i).getDealAbstract());
                bundle.putString(nameField + "Description", newDealList.get(i).getDealDescription());
                bundle.putString(nameField + "Value", newDealList.get(i).getDealValue());
                bundle.putString(nameField + "Category", newDealList.get(i).getCATSTRING());//method to get single

                newDealsCount++;
            }

            bundle.putInt("newDealsCount", newDealsCount);
            processStartService(context, NotificationService.TAG, bundle);
            createNotification(context);
        }
    }

    public void createNotification(Context context) {
        try {
            Intent intent = new Intent(context, DealsActivity.class);
            intent.putExtra("fromNotification", true);
            PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);

            Notification n = new Notification.Builder(context)
                    .setContentTitle("New Deals")
                    .setContentText("Touch to view New Deals")
                    //.setSmallIcon(R.drawable.head)
                    .setSmallIcon(R.drawable.ic_circle_d)
                    .setContentIntent(pIntent)
                    .setAutoCancel(true)
                    .build();

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, n);
        } catch (IllegalArgumentException ex) {
            Log.v("CreatingNotification", ex.getMessage());
        } catch (Exception ex) {
            Log.v("CreatingNotification", ex.getMessage());
        }
    }

    private boolean isMyServiceRunning(Context context,Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void processStartService(Context contex, final String tag, Bundle bundle) {
        try {
            Intent intent = new Intent(contex, NotificationService.class);
            intent.putExtras(bundle);
            intent.addCategory(tag);
            contex.startService(intent);
        } catch (Exception ex) {
            Log.v("Service", ex.getMessage());
        }
    }
}
