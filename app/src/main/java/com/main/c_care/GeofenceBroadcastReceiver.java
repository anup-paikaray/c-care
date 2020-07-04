package com.main.c_care;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "GeofenceBroadcast";

    @Override
    public void onReceive(Context context, Intent intent) {

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

            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT)
                notificationHelper.sendHighPriorityNotification("YOU ARE SAFE NOW", "Thanks for getting out", MapsActivity.class);
        }

        if (geofenceId % 10 == 0) {
            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER)
                notificationHelper.sendHighPriorityNotification("PLEASE WASH HANDS FOR 20 SECS", "Think about your family once", MapsActivity.class);

            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT)
                notificationHelper.sendHighPriorityNotification("PLEASE WEAR MASK", "Keep yourself and everyone else safe", MapsActivity.class);
        }

        if (geofenceId % 10 == 2) {
            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER)
                notificationHelper.sendHighPriorityNotification("WORK PLACE ENTERED", "Sanitize your hand", MapsActivity.class);
            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT)
                notificationHelper.sendHighPriorityNotification("WORK PLACE EXIT", "Please wear mask", MapsActivity.class);
        }
    }
}