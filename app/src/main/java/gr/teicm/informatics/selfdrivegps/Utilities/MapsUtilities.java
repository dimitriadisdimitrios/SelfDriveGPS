package gr.teicm.informatics.selfdrivegps.Utilities;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import gr.teicm.informatics.selfdrivegps.Controller.Controller;
import gr.teicm.informatics.selfdrivegps.FieldMath.MultiPolylineAlgorithm;
import gr.teicm.informatics.selfdrivegps.Fragment.DialogFragment;
import gr.teicm.informatics.selfdrivegps.Fragment.DialogRadioFragment;
import gr.teicm.informatics.selfdrivegps.R;

public class MapsUtilities {
    private static final String TAG = "MapsUtilities";
    private static final int setTimeOnCounters = 1500; //1.5 sec
    private static Controller controller = new Controller();
    private static Handler handler = new Handler();
    private static Runnable runnableForModes, runnableForSpeed;


    public static void showAlertDialog(android.app.FragmentManager fragmentManager){
        DialogFragment dialogFragment = new DialogFragment();
        dialogFragment.show(fragmentManager, "Main Dialog for multiple uses");
        dialogFragment.setCancelable(false); //prevent dialog box from getting dismissed on back key
    }

    public static boolean hasPermissions(Context context, String... allPermissionNeeded) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && context != null && allPermissionNeeded != null)
            for (String permission : allPermissionNeeded)
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
                    return false;
        return true;
    }

    //All Permissions i need for android 6.0 and above
    public static void checkLocationPermission(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permissions check passed");
            }
        }
    }

    public static void changeLabelAboutMode(TextView label, ToggleButton startStopTBtn){
        String modeOfApp = controller.getProgramStatus();
        switch (modeOfApp){
            case Controller.MODE_1_RECORD_FIELD:
                label.setText(String.format("Mode: %s", Controller.MODE_1_RECORD_FIELD));
                break;
            case Controller.MODE_2_CREATE_LINE:
                label.setText(String.format("Mode: %s", Controller.MODE_2_CREATE_LINE));
                break;
            case Controller.MODE_3_DRIVING:
                label.setText(String.format("Mode: %s", Controller.MODE_3_DRIVING));
                startStopTBtn.setVisibility(View.INVISIBLE);
                break;
        }
    }

    public static void placePolylineForRoute(ArrayList<LatLng> directionPoints, GoogleMap googleMap) {
        PolylineOptions polylineOptions = new PolylineOptions()
                .width(5)
                .color(Color.RED)
                .addAll(directionPoints);
        googleMap.addPolyline(polylineOptions);
    }

    public static void placePolylineParallel(ArrayList<LatLng> directionPoints, GoogleMap googleMap) {
        PolylineOptions polylineOptions = new PolylineOptions()
                .width(5)
                .color(Color.BLUE)
                .addAll(directionPoints);
        googleMap.addPolyline(polylineOptions);
    }

    public static void placePolygonForRoute(ArrayList<LatLng> directionPoints, GoogleMap googleMap){
        PolygonOptions polygonOptions = new PolygonOptions()
                .fillColor(Color.TRANSPARENT)
                .strokeColor(Color.GREEN)
                .strokeWidth(5)
                .addAll(directionPoints);
        googleMap.addPolygon(polygonOptions);
    }

    public static void getSpecsForStatusBar(float speed, float accuracy, TextView mSpeed, TextView mAccuracy, Context context){
        float kmH = (float) (speed *3.6); //Convert m/s to km/h

        mSpeed.setText(context.getString(R.string.speed_counter, kmH));
        mAccuracy.setText(context.getString(R.string.accuracy_of_gps, accuracy));
    }

    //Check if user moving. If it stay still the counter start to reset speed and accuracy
    public static void checkIfUserStandStill(final TextView mSpeed, final TextView mAccuracy, final Context context){
        handler.removeCallbacks(runnableForSpeed);
        runnableForSpeed = new Runnable() {
            @Override
            public void run() {
                MapsUtilities.getSpecsForStatusBar(0,0, mSpeed, mAccuracy, context);
            }
        };
        handler.postDelayed(runnableForSpeed, setTimeOnCounters);
    }
    public static void checkIfModeChanged(final TextView textView, final ToggleButton toggleButton){
        runnableForModes = new Runnable() {
            @Override
            public void run() {
                changeLabelAboutMode(textView, toggleButton);
                handler.postDelayed(runnableForModes,setTimeOnCounters);
            }
        };
        handler.postDelayed(runnableForModes, setTimeOnCounters);
    }
    //TODO: After finish of navigationAlgorithm I can use it
    public static void recreateFieldWithMultiPolyline(GoogleMap mMap){
        mMap.clear();
        MapsUtilities.placePolygonForRoute(controller.getArrayListForField(), mMap);
        MultiPolylineAlgorithm.algorithmForCreatingPolylineInField(controller.getArrayListForLine());
        MapsUtilities.placePolylineForRoute(controller.getArrayListForLine(),mMap);
        for(int i = 0; i<controller.getArrayListOfMultipliedPolyLines().size(); i++){
            MapsUtilities.placePolylineForRoute(controller.getArrayListOfMultipliedPolyLines().get(i), mMap);
        }
    }

    public static Boolean checkingInWhichPolylineUserEntered(LatLng currentLocation){
        Controller controller = new Controller();
        Boolean focusOnSpecificPlace = false;

        for(ArrayList<LatLng> focusedPolyline : controller.getArrayListOfMultipliedPolyLines()){
            focusOnSpecificPlace = ApproachPolylineUtilities.bdccGeoDistanceCheckWithRadius(focusedPolyline, currentLocation, Controller.MAIN_RADIUS_TO_RECOGNISE_POLYLINE);
            if(focusOnSpecificPlace){
                controller.setArrayListForLineToFocus(focusedPolyline);
                break;
            }
        }
        return focusOnSpecificPlace;
    }
}