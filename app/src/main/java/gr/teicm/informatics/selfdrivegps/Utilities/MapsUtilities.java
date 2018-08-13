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
import gr.teicm.informatics.selfdrivegps.FieldMath.NavigationPolylineAlgorithm;
import gr.teicm.informatics.selfdrivegps.Fragment.DialogFragment;
import gr.teicm.informatics.selfdrivegps.Fragment.DialogFragmentRadio;
import gr.teicm.informatics.selfdrivegps.R;

public class MapsUtilities {
    private static final String TAG = "MapsUtilities";
    private static final int setTimeForCheckSpeedAccuracy = 1500 /*1.5 sec*/, setTimeOnCounterForChecks = 4000 /*4 sec*/;
    private static Controller controller = new Controller();
    private static Handler handler = new Handler();
    private static Runnable runnableForModes;
    private static Runnable runnableForSpeed;

    public static void showAlertDialog(android.app.FragmentManager fragmentManager){
        DialogFragment dialogFragment = new DialogFragment();
        dialogFragment.show(fragmentManager, "Main Dialog for multiple uses");
        dialogFragment.setCancelable(false); //prevent dialog box from getting dismissed on back key
    }
    public static void showAlertDialogRadio(android.app.FragmentManager fragmentManager){
        DialogFragmentRadio dialogFragmentRadio = new DialogFragmentRadio();
        dialogFragmentRadio.show(fragmentManager, "Dialog only to change terrain on map");
        dialogFragmentRadio.setCancelable(false); //prevent dialog box from getting dismissed on back key
    }

    //All Permissions i need for android 6.0 and above
    public static void checkLocationPermission(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permissions check passed");
            }
        }
    }
    public static boolean hasPermissions(Context context, String... allPermissionNeeded) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && context != null && allPermissionNeeded != null)
            for (String permission : allPermissionNeeded)
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
                    return false;
        return true;
    }

    public static void placePolylineForRoute(ArrayList<LatLng> directionPoints, GoogleMap googleMap) {
        PolylineOptions polylineOptions = new PolylineOptions()
                .width(5)
                .color(Color.RED)
                .addAll(directionPoints);
        googleMap.addPolyline(polylineOptions);
    }
    private static void placePolylineParallel(ArrayList<LatLng> directionPoints, GoogleMap googleMap) {
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

    public static void counterToCheckIfUserStandStill(final TextView mSpeed, final TextView mAccuracy, final Context context){ //Counter to check if user moving. If it stay still the counter start to reset speed and accuracy
        handler.removeCallbacks(runnableForSpeed);
        runnableForSpeed = new Runnable() {
            @Override
            public void run() {
                MapsUtilities.getSpecsForStatusBar(0,0, mSpeed, mAccuracy, context);
                handler.postDelayed(runnableForSpeed, setTimeForCheckSpeedAccuracy);
            }
        };
        handler.postDelayed(runnableForSpeed, setTimeForCheckSpeedAccuracy);
    }
    public static void counterToCheckIfModeChanged(final TextView textView, final ToggleButton toggleButton){ //Counter to check in which mode program are
        runnableForModes = new Runnable() {
            @Override
            public void run() {
                changeLabelAboutMode(textView, toggleButton);
                handler.postDelayed(runnableForModes, setTimeForCheckSpeedAccuracy);
            }
        };
        handler.postDelayed(runnableForModes, setTimeForCheckSpeedAccuracy);
    }
    public static void counterToCheckIfArrayListIsEmpty(final ToggleButton toggleButton){ //Counter to make sure the tBtn start/stop wouldn't double-pressed
        Runnable runnableForTBtnClickAbility = new Runnable() {
            @Override
            public void run() {
                switch (controller.getProgramStatus()) {
                    case Controller.MODE_1_RECORD_FIELD:
                        if (controller.getArrayListForField() == null) {
                            handler.postDelayed(runnableForModes, setTimeOnCounterForChecks);
                        } else {
                            toggleButton.setClickable(true);
                        }
                        break;
                    case Controller.MODE_2_CREATE_LINE:
                        if (controller.getArrayListForLine() == null) {
                            handler.postDelayed(runnableForModes, setTimeOnCounterForChecks);
                        } else {
                            toggleButton.setClickable(true);
                        }
                        break;
                }
            }
        };
        handler.postDelayed(runnableForTBtnClickAbility, setTimeOnCounterForChecks);
    }

    // Re-draw the map. Use it as default function
    public static void recreateFieldWithMultiPolyline(GoogleMap mMap){
        mMap.clear(); //clear the map
        MapsUtilities.placePolygonForRoute(controller.getArrayListForField(), mMap); //Create field
        MapsUtilities.placePolylineForRoute(controller.getArrayListForLine(),mMap); //TODO: Temporary use for working with navigationAlgorithmV2

        MultiPolylineAlgorithm.algorithmForCreatingPolylineInField(controller.getArrayListForLine()); //Algorithm to create multi-polyLine
        for(int i = 0; i<controller.getArrayListOfMultipliedPolyLines().size(); i++){
            MapsUtilities.placePolylineForRoute(controller.getArrayListOfMultipliedPolyLines().get(i), mMap); // Draw the multi-polyLines on map
        }
    }

    //Recognize in which polyline you are
    private static Boolean checkingInWhichPolylineUserEntered(LatLng currentLocation){
        Boolean focusOnSpecificPlace = false;

        for(ArrayList<LatLng> focusedPolyline : controller.getArrayListOfMultipliedPolyLines()){ // Set polyLines to test it about which one is the user
            focusOnSpecificPlace = ApproachPolylineUtilities.bdccGeoDistanceCheckWithRadius(focusedPolyline, currentLocation, Controller.MAIN_RADIUS_TO_RECOGNISE_MAIN_POLYLINE);
            if(focusOnSpecificPlace){
                controller.setArrayListForLineToFocus(focusedPolyline); //Set it on controller to get then number of index to show it on MapsActivity
                break;
            }
        }
        return focusOnSpecificPlace;
    }

    //Function to generate invisible parallel
    public static void generateTempParallelPolyLines(GoogleMap googleMap, LatLng mCurrentLocation){

        if(checkingInWhichPolylineUserEntered(mCurrentLocation)){ //Check if user is in anyone of MultiPolyLines
            //Create the parallel lines to given //TODO: Need a lot of work
            ArrayList<ArrayList<LatLng>> parPolyline = NavigationPolylineAlgorithm.algorithmForCreatingTwoInvisibleParallelPolylineForNavigation(controller.getArrayListForLineToFocus()); //Get the main ArrayList to generate the 2 polyLines

            for(ArrayList<LatLng> temp : parPolyline){
                MapsUtilities.placePolylineParallel(temp, googleMap); //Place the 2 PolyLines on map
            }
        }else{
            recreateFieldWithMultiPolyline(googleMap); // Secure that after move out of the specific ArrayList, the map while come to his normal
        }
    }
}