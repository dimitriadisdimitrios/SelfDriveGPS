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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import gr.teicm.informatics.selfdrivegps.Controller.Controller;
import gr.teicm.informatics.selfdrivegps.FieldMath.MultiPolylineAlgorithm;
import gr.teicm.informatics.selfdrivegps.Fragment.DialogChangeTerrain;
import gr.teicm.informatics.selfdrivegps.Fragment.DialogMainFunction;
import gr.teicm.informatics.selfdrivegps.R;

public class MapsUtilities {
    private static final String TAG = "MapsUtilities";
    private static final int setTimeForCheckSpeedAccuracy = 1500; /*1.5 sec*/
    private static final int setTimeOnCounterForChecks = 4000 /*4 sec*/;
    private static Controller controller = new Controller();
    private static Handler handler = new Handler();
    private static Runnable runnableForModes, runnableForSpeed, runnableForTBtnClickAbility;

    public static void showAlertDialog(android.app.FragmentManager fragmentManager){
        DialogMainFunction dialogMainFunction = new DialogMainFunction();
        dialogMainFunction.show(fragmentManager, "Main Dialog for multiple uses");
        dialogMainFunction.setCancelable(false); //prevent dialog box from getting dismissed on back key
    }
    public static void showAlertDialogRadio(android.app.FragmentManager fragmentManager){
        DialogChangeTerrain dialogChangeTerrain = new DialogChangeTerrain();
        dialogChangeTerrain.show(fragmentManager, "Dialog only to change terrain on map");
        dialogChangeTerrain.setCancelable(false); //prevent dialog box from getting dismissed on back key
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
    } //Draw the main\multi lines
    public static void placePolylineParallel(ArrayList<LatLng> directionPoints, GoogleMap googleMap) {
        PolylineOptions polylineOptions = new PolylineOptions()
                .width(5)
                .color(Color.BLUE)
                .addAll(directionPoints);
        googleMap.addPolyline(polylineOptions);
    } //Draw the parallel lines
    public static void placePolygonForRoute(ArrayList<LatLng> directionPoints, GoogleMap googleMap){
        PolygonOptions polygonOptions = new PolygonOptions()
                .fillColor(Color.TRANSPARENT)
                .strokeColor(Color.GREEN)
                .strokeWidth(5)
                .addAll(directionPoints);
        googleMap.addPolygon(polygonOptions);
    } //Draw the polygon for field

    public static void getSpecsForStatusBar(float speed, float accuracy, TextView mSpeed, TextView mAccuracy, Context context){
        float kmH = (float) (speed *3.6); //Convert m/s to km/h
        mSpeed.setText(context.getString(R.string.speed_counter, kmH));
        mAccuracy.setText(context.getString(R.string.accuracy_of_gps, accuracy));
    }

    public static void changeLabelAboutMode(TextView label, ToggleButton startStopTBtn, RelativeLayout rlNavBar, ImageButton iBtnRangeMeter){
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
                rlNavBar.setVisibility(View.VISIBLE);
                iBtnRangeMeter.setVisibility(View.VISIBLE);
                break;
        }
    }
    //It has the job to rotate whole arrow based on user bearing
    public static void changeRotationOnUserLocationArrow(RelativeLayout wholeArrow, Float userBearing){
        wholeArrow.setRotation(userBearing);
    }
    //Use it to set bearing of map camera
    public static Float changeBearingOfCameraBasedOnMode(Float userBearing){
        if(controller.getProgramStatus().equals(Controller.MODE_3_DRIVING)){
            return userBearing;
        }else{
            return (float) 0;
        }
    }
    //Use it to set zoom of map camera
    public static Integer changeZoomOfCameraBasedOnMode(){
        if(controller.getProgramStatus().equals(Controller.MODE_3_DRIVING)){
            return 20;
        }else{
            return 16;
        }
    }
    //Use it to set tilt of map camera
    public static Integer changeTiltOfCameraBasedOnMode(){
        if(controller.getProgramStatus().equals(Controller.MODE_3_DRIVING)){
            return 90;
        }else{
            return 0;
        }
    }

    //Counters for speed, gps-accuracy, to check which mode is enabled
    public static void counterToCheckIfModeChanged(final TextView textView, final ToggleButton toggleButton, final RelativeLayout rlNavBar, final ImageButton iBtnRangeMeter){
        runnableForModes = new Runnable() {
            @Override
            public void run() {
                changeLabelAboutMode(textView, toggleButton, rlNavBar, iBtnRangeMeter);
                if(!controller.getProgramStatus().equals(Controller.MODE_3_DRIVING)){
                    handler.postDelayed(runnableForModes, setTimeForCheckSpeedAccuracy);
                }else{
                    handler.removeCallbacks(runnableForModes);
                }
            }
        };
        handler.postDelayed(runnableForModes, setTimeForCheckSpeedAccuracy);
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
    public static void counterToCheckIfArrayListIsEmpty(final ToggleButton toggleButton){ //Counter to make sure the tBtn start/stop wouldn't double-pressed
        runnableForTBtnClickAbility = new Runnable() {
            @Override
            public void run() {
                switch (controller.getProgramStatus()) {
                    case Controller.MODE_1_RECORD_FIELD:
                        if (controller.getArrayListForField() == null) {
                            handler.postDelayed(runnableForTBtnClickAbility, setTimeOnCounterForChecks);
                        } else {
                            toggleButton.setClickable(true);
                        }
                        break;
                    case Controller.MODE_2_CREATE_LINE:
                        if (controller.getArrayListForLine() == null) {
                            handler.postDelayed(runnableForTBtnClickAbility, setTimeOnCounterForChecks);
                        } else {
                            toggleButton.setClickable(true);
                        }
                        break;
                }
            }
        };
        handler.postDelayed(runnableForTBtnClickAbility, setTimeOnCounterForChecks);
    }

    public static void turnOnOffLightBehindNavigationBarToSetCourse(ImageView rightCube, ImageView leftCube, ImageView midCube){
        switch(controller.getLocationOfUserForNavigationBar()){
            case Controller.LEFT:
                rightCube.setVisibility(View.INVISIBLE);
                leftCube.setVisibility(View.VISIBLE);
                midCube.setVisibility(View.INVISIBLE);
                break;
            case Controller.RIGHT:
                rightCube.setVisibility(View.VISIBLE);
                leftCube.setVisibility(View.INVISIBLE);
                midCube.setVisibility(View.INVISIBLE);
                break;
            case Controller.MID:
                rightCube.setVisibility(View.INVISIBLE);
                leftCube.setVisibility(View.INVISIBLE);
                midCube.setVisibility(View.VISIBLE);
                break;
            case Controller.NONE:
                rightCube.setVisibility(View.INVISIBLE);
                leftCube.setVisibility(View.INVISIBLE);
                midCube.setVisibility(View.INVISIBLE);
                break;
        }
    }

    // Re-draw the map. Use it as default function
    public static void recreateFieldWithMultiPolyline(GoogleMap mMap){
        mMap.clear(); //clear the map
        if(controller.getProgramStatus().equals(Controller.MODE_2_CREATE_LINE)){
            MapsUtilities.placePolygonForRoute(controller.getArrayListForField(), mMap); //Create field
        }else if(controller.getProgramStatus().equals(Controller.MODE_3_DRIVING)){

            MapsUtilities.placePolygonForRoute(controller.getArrayListForField(), mMap); //Create field
            MultiPolylineAlgorithm.algorithmForCreatingPolylineInField(controller.getArrayListForLine()); //Algorithm to create multi-polyLine
            for(int i = 0; i<controller.getArrayListOfMultipliedPolyLines().size(); i++){
                MapsUtilities.placePolylineForRoute(controller.getArrayListOfMultipliedPolyLines().get(i), mMap); // Draw the multi-polyLines on map
            }
        }
    }
}