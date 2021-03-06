package com.main.c_care.geofence;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.main.c_care.database.LocalDatabaseHelper;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "GeofenceBroadcast";
    LocalDatabaseHelper myDb;

    private void countExits(Context context, int id) {
        int count = 0;
        Cursor res = myDb.getLocationData();
        if (res.getCount() == 0) {
            return;
        }
        else {
            while (res.moveToNext()) {
                if (id == res.getInt(0))
                    count = res.getInt(5);
            }
        }
        boolean isCounted = myDb.updateCount(id, count + 1);
        if (isCounted)
            Toast.makeText(context, "DATA UPDATED", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        myDb = new LocalDatabaseHelper(context);
        NotificationHelper notificationHelper = new NotificationHelper(context);

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent.hasError()) {
            Log.d(TAG, "onReceive: Error receiving geofence event...");
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        int geofenceId = Integer.parseInt(geofencingEvent.getTriggeringGeofences().get(0).getRequestId());
        Log.d(TAG, "Geofence Triggered: " + geofenceId);

        if (geofenceId % 10 == 0) {
            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER)
                notificationHelper.sendHighPriorityNotification("PLEASE WASH HANDS FOR 20 SECS", "Think about your family once", MapsActivity.class);

            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                countExits(context,geofenceId / 10);
                notificationHelper.sendHighPriorityNotification("PLEASE WEAR MASK", "Keep yourself and everyone else safe", MapsActivity.class);
            }
        }

        if (geofenceId % 10 == 1) {
            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER)
                notificationHelper.sendHighPriorityNotification("HOTSPOT ALERT", "Stay away from Hotspots", MapsActivity.class);

            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL)
                notificationHelper.sendHighPriorityNotification("YOU MIGHT BE IN DANGER", "Fall back quickly", MapsActivity.class);

            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                countExits(context,geofenceId / 10);
                notificationHelper.sendHighPriorityNotification("YOU ARE SAFE NOW", "Thanks for getting out", MapsActivity.class);
            }
        }

        if (geofenceId % 10 == 2) {
            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER)
                notificationHelper.sendHighPriorityNotification("WORK PLACE ENTERED", "Sanitize your hand", MapsActivity.class);
            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                countExits(context,geofenceId / 10);
                notificationHelper.sendHighPriorityNotification("WORK PLACE EXIT", "Please wear mask", MapsActivity.class);
            }
        }
    }
}