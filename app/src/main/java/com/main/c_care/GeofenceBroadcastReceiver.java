package com.main.c_care;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.maps.model.LatLng;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "GeofenceBroadcast";
    DatabaseHelper myDb;

    private void countExits(int id) {
        int count = 0;
        Cursor res = myDb.getAllData();
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
            Toast.makeText(null, "DATA UPDATED", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        myDb = new DatabaseHelper(context);
        NotificationHelper notificationHelper = new NotificationHelper(context);

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent.hasError()) {
            Log.d(TAG, "onReceive: Error receiving geofence event...");
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        int geofenceId = Integer.parseInt(geofencingEvent.getTriggeringGeofences().get(0).getRequestId());
        Log.d(TAG, "Geofence Triggered: " + geofenceId);

        if (geofenceId % 10 == 1) {
            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER)
                notificationHelper.sendHighPriorityNotification("HOTSPOT ALERT", "Stay away from Hotspots", MapsActivity.class);

            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL)
                notificationHelper.sendHighPriorityNotification("YOU MIGHT BE IN DANGER", "Fall back quickly", MapsActivity.class);

            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                countExits(geofenceId / 10);
                notificationHelper.sendHighPriorityNotification("YOU ARE SAFE NOW", "Thanks for getting out", MapsActivity.class);
            }
        }

        if (geofenceId % 10 == 0) {
            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER)
                notificationHelper.sendHighPriorityNotification("PLEASE WASH HANDS FOR 20 SECS", "Think about your family once", MapsActivity.class);

            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                countExits(geofenceId / 10);
                notificationHelper.sendHighPriorityNotification("PLEASE WEAR MASK", "Keep yourself and everyone else safe", MapsActivity.class);
            }
        }

        if (geofenceId % 10 == 2) {
            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER)
                notificationHelper.sendHighPriorityNotification("WORK PLACE ENTERED", "Sanitize your hand", MapsActivity.class);
            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                countExits(geofenceId / 10);
                notificationHelper.sendHighPriorityNotification("WORK PLACE EXIT", "Please wear mask", MapsActivity.class);
            }
        }
    }
}