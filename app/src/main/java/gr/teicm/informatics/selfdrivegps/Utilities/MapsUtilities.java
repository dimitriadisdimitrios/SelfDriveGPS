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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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
    private static ArrayList<LatLng> mInner = new ArrayList<>();
    private static ArrayList<LatLng> mPointForMainLine = new ArrayList<>();
    private static ArrayList<ArrayList<LatLng>> mOuter = new ArrayList<>();
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
                .color(Color.parseColor("#000000"))
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
    public static void placePassedPlace(ArrayList<LatLng> directionPoints, GoogleMap googleMap){
        PolylineOptions polylineOptions = new PolylineOptions()
                .width(20)
                .color(Color.parseColor("#992FA72F"))
                .addAll(directionPoints);
        googleMap.addPolyline(polylineOptions);
    } //Draw the polygon for field

    public static void getSpecsForStatusBar(float speed, float accuracy, TextView mSpeed, TextView mAccuracy, Context context){
        float kmH = (float) (speed *3.6); //Convert m/s to km/h
        mSpeed.setText(context.getString(R.string.speed_counter, kmH));
        mAccuracy.setText(context.getString(R.string.accuracy_of_gps, accuracy));
    }

    public static void changeLabelAboutMode(TextView label, ToggleButton startStopTBtn, ToggleButton coverPassedTBtn, RelativeLayout rlNavBar, ImageView iBtnRangeMeter){
        String modeOfApp = controller.getProgramStatus();
        switch (modeOfApp){
            case Controller.MODE_1_RECORD_FIELD:
                label.setText(String.format("Mode: %s", Controller.MODE_1_RECORD_FIELD));
                rlNavBar.setVisibility(View.GONE);
                break;
            case Controller.MODE_2_CREATE_LINE:
                label.setText(String.format("Mode: %s", Controller.MODE_2_CREATE_LINE));
                rlNavBar.setVisibility(View.GONE);
                break;
            case Controller.MODE_3_DRIVING:
                label.setText(String.format("Mode: %s", Controller.MODE_3_DRIVING));
                coverPassedTBtn.setVisibility(View.VISIBLE);
                startStopTBtn.setVisibility(View.GONE);
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

    //Listener for long touch on map
    public static void listenerForTouchAddOfMainLine(){
        final GoogleMap mMap = controller.getGoogleMap();

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng point) {

                //Check if markers are 1 or 0 and the program is in right mode
                if (mPointForMainLine.size() < 2 && controller.getProgramStatus().equals(Controller.MODE_2_CREATE_LINE)) {
                    //TODO: Remove comments

                    MarkerOptions marker = new MarkerOptions().position(new LatLng(point.latitude, point.longitude));
                    mPointForMainLine.add(point);
                    controller.setMarkerPosition(mPointForMainLine); //To save the spots of Marker

                    if (controller.getMarkerPosition().size() >= 2) {
                        MapsUtilities.placePolylineForRoute(controller.getMarkerPosition(), mMap);
                    }
                    mMap.addMarker(marker);
                }
            }
        });
    }
    //Listener for clicking a Marker to add line on field
    public static void listenerForClickOnMarkers(){
        final GoogleMap mMap = controller.getGoogleMap();

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //Remove the lat/lng from controller
                controller.getMarkerPosition().remove(controller.getMarkerPosition().indexOf(marker.getPosition()));
                //Remove Marker
                marker.remove();
                //Re-draw the map
                recreateFieldWithMultiPolyline(controller.getGoogleMap());
                // True to not re-enter
                return true;
            }
        });
    }

    //Counters for speed, gps-accuracy, to check which mode is enabled
    public static void counterToCheckIfModeChanged(final TextView textView, final ToggleButton startStopTBtn, final ToggleButton coverPassedTBtn, final RelativeLayout rlNavBar, final ImageView iBtnRangeMeter){
        runnableForModes = new Runnable() {
            @Override
            public void run() {
                changeLabelAboutMode(textView, startStopTBtn, coverPassedTBtn, rlNavBar, iBtnRangeMeter);
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

    //Show the right animation for navigation purpose
    public static void turnOnOffLightForNavigationBarToSetCourse(ImageView rightWay, ImageView leftWay, ImageView midPoint, Context context){
        Animation rightAnim = AnimationUtils.loadAnimation(context, R.anim.nav_arrow_right_mode);
        Animation leftAnim = AnimationUtils.loadAnimation(context, R.anim.nav_arrow_left_mode);
        Animation centerAnim = AnimationUtils.loadAnimation(context, R.anim.fade_in_out_center);


        switch(controller.getLocationOfUserForNavigationBar()){
            case Controller.LEFT:
                rightWay.setVisibility(View.GONE);
                leftWay.setVisibility(View.VISIBLE);
                midPoint.setVisibility(View.GONE);

                rightWay.clearAnimation();
                midPoint.clearAnimation();
                leftWay.startAnimation(leftAnim);
                break;
            case Controller.RIGHT:
                rightWay.setVisibility(View.VISIBLE);
                leftWay.setVisibility(View.GONE);
                midPoint.setVisibility(View.GONE);

                leftWay.clearAnimation();
                midPoint.clearAnimation();
                rightWay.startAnimation(rightAnim);
                break;
            case Controller.MID:
                rightWay.setVisibility(View.GONE);
                leftWay.setVisibility(View.GONE);
                midPoint.setVisibility(View.VISIBLE);

                rightWay.clearAnimation();
                leftWay.clearAnimation();
                midPoint.startAnimation(centerAnim);
                break;
            case Controller.NONE:
                rightWay.setVisibility(View.GONE);
                leftWay.setVisibility(View.GONE);
                midPoint.setVisibility(View.GONE);

                midPoint.clearAnimation();
                leftWay.clearAnimation();
                rightWay.clearAnimation();
                break;
        }
    }

    //Draw the route of user pass
    public static void createCoverRouteUserPass(LatLng mLocation, Boolean toggleButton){
        if(toggleButton && FieldFunctionsUtilities.PointIsInRegion(mLocation, controller.getArrayListForField())){
            mInner.add(mLocation);
            placePassedPlace(mInner, controller.getGoogleMap());
        }else if(mInner != null && (!FieldFunctionsUtilities.PointIsInRegion(mLocation, controller.getArrayListForField()) || !toggleButton)){
            ArrayList<LatLng> myTemp = new ArrayList<>(mInner);
            mOuter.add(myTemp);
            controller.setArrayListOfPassedPolyLines(mOuter);
            mInner.clear();
        }
    }
    // Re-draw the map. Use it as default function
    public static void recreateFieldWithMultiPolyline(GoogleMap mMap){
        mMap.clear(); //clear the map

        if(controller.getProgramStatus().equals(Controller.MODE_2_CREATE_LINE)){

            MapsUtilities.placePolygonForRoute(controller.getArrayListForField(), mMap); //Create field

            if(controller.getMarkerPosition() != null){
                for(int i=0; i<controller.getMarkerPosition().size(); i++){
                    MarkerOptions marker = new MarkerOptions().position(new LatLng(controller.getMarkerPosition().get(i).latitude, controller.getMarkerPosition().get(i).longitude));
                    mMap.addMarker(marker);
                }
            }
        }else if(controller.getProgramStatus().equals(Controller.MODE_3_DRIVING)){
            if(controller.getArrayListOfPlacedPolyLines() != null){
                for(int j=0; j < controller.getArrayListOfPlacedPolyLines().size(); j++){
                    placePassedPlace(controller.getArrayListOfPlacedPolyLines().get(j), controller.getGoogleMap());
                }
            }

            MapsUtilities.placePolygonForRoute(controller.getArrayListForField(), mMap); //Create field
            MultiPolylineAlgorithm.algorithmForCreatingPolylineInField(controller.getArrayListForLine()); //Algorithm to create multi-polyLine
            for(int i = 0; i<controller.getArrayListOfMultipliedPolyLines().size(); i++){
                MapsUtilities.placePolylineForRoute(controller.getArrayListOfMultipliedPolyLines().get(i), mMap); // Draw the multi-polyLines on map
            }
        }
    }
}