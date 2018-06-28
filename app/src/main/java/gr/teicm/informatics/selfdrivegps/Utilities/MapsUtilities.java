package gr.teicm.informatics.selfdrivegps.Utilities;

import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;

public class MapsUtilities {
    private static String TAG = "MapsUtilities";
    private static ArrayList<LatLng> arrayList;

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
    public static void geofence(String id, LatLng latLng, GoogleApiClient googleApiClient,PendingIntent pendingIntent, Context context) {

        Geofence geofence = new Geofence.Builder()
                .setRequestId(id)
                .setCircularRegion(latLng.latitude, latLng.longitude, 50)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setNotificationResponsiveness(1000)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();

        GeofencingRequest geofencingRequest = new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(geofence)
                .build();

        //Check if Geofence has been added
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {return;}
        LocationServices.GeofencingApi.addGeofences(googleApiClient, geofencingRequest, pendingIntent)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            Log.d(TAG, "Successfully added to geofence");
                        } else {
                            Log.d(TAG, "Failed to add geofence");
                            Log.d(TAG, "Called... FAILURE: " + status.getStatusMessage() + " code: " + status.getStatusCode());
                        }
                    }
                });
    }

    public static CircleOptions createCircleOptions(LatLng centerOfPolygon, double coverFieldRange){
        CircleOptions circleOptions = new CircleOptions();
        return circleOptions.strokeColor(Color.BLACK)
                .fillColor(Color.TRANSPARENT)
                .center(centerOfPolygon)
                .radius(coverFieldRange);
    }

    public static boolean checkIfLatLngExist(LatLng latLng, ArrayList<LatLng> points){
        boolean latLngExist = false;
        for(int i=0; i<points.size(); i++){
            if(points.get(i)==latLng){
                latLngExist=true;
            }
        }
        return latLngExist;
    }
    //TODO: See when function used
    public static boolean hasPermissions(Context context, String... allPermissionNeeded) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && context != null && allPermissionNeeded != null)
            for (String permission : allPermissionNeeded)
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
                    return false;
        return true;
    }
    //Setter for ArrayList<LatLng>
    public void setPoints(ArrayList<LatLng> points){
        arrayList = points;
    }
    //Getter for ArrayList<LatLng>
    public ArrayList<LatLng> getPoints(){
        return arrayList;
    }
}

