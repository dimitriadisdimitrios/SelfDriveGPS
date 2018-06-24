package gr.teicm.informatics.selfdrivegps;

import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;

public class MapsUtilities {
    private static String TAG = "MapsUtilities";

    //It find the center of polygon
    public static LatLng getPolygonCenterPoint(ArrayList<LatLng> polygonPointsList) {
        LatLng centerLatLng;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (int i = 0; i < polygonPointsList.size(); i++) {
            builder.include(polygonPointsList.get(i));
        }
        LatLngBounds bounds = builder.build();
        centerLatLng = bounds.getCenter();

        return centerLatLng;
    }

    //Create Geo fence objects
    public static Geofence createGeofenceObject(String id, LatLng latLng) {

        Geofence geofence = new Geofence.Builder()
                .setRequestId(id)
                .setCircularRegion(latLng.latitude, latLng.longitude, 50)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setNotificationResponsiveness(1000)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();
        return geofence;
    }

    public static GeofencingRequest createGeofencingRequest(Geofence geofence) {

        GeofencingRequest geofencingRequest = new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(geofence)
                .build();
        return geofencingRequest;
    }

    //Check if Geofence has been added
    public static void checkIfGeoFenceHasBeenAdded(GoogleApiClient googleApiClient, GeofencingRequest geofencingRequest, PendingIntent pendingIntent, Context context) {
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {return;}
        LocationServices.GeofencingApi.addGeofences(googleApiClient, geofencingRequest, pendingIntent)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
//                            Log.d(TAG, String.valueOf(status.getStatusCode()));
                            Log.d(TAG, "Successfully added to geofence");
                        } else {
                            Log.d(TAG, "Failed to add geofence");
                            Log.d(TAG, "Called... FAILURE: " + status.getStatusMessage() + " code: " + status.getStatusCode());
                        }
                    }
                });
    }
}

