package gr.teicm.informatics.selfdrivegps;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

public class GeofenceService extends IntentService {
    public static final String TAG = "GeofenceService";

    public GeofenceService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);

        int transition = event.getGeofenceTransition();
        List<Geofence> geofences = event.getTriggeringGeofences();
        Geofence geofence = geofences.get(0);
        String requestId = geofence.getRequestId();

        if (transition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            Log.d(TAG, "Entering geofence - " + requestId);
//            Intent viewIntent = new Intent(GeofenceService.this, PopMenuActivity.class);
//            viewIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(viewIntent);
        } else if (transition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            Log.d(TAG, "Exiting geofence - " + requestId);
        } else if (event.hasError()) {
            Log.d(TAG, "Houston we have a problem - " + requestId + transition);
        }
    }
}

