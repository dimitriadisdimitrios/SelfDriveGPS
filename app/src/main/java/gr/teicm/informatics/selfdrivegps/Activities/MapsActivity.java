package gr.teicm.informatics.selfdrivegps.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;

import java.util.ArrayList;

import gr.teicm.informatics.selfdrivegps.Utilities.FieldFunctionsUtilities;
import gr.teicm.informatics.selfdrivegps.R;
import gr.teicm.informatics.selfdrivegps.Controller.Controller;
import gr.teicm.informatics.selfdrivegps.Utilities.MapsUtilities;
import gr.teicm.informatics.selfdrivegps.Utilities.PermissionUtilities;

public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = "MapsActivity";
    private static final long MIN_TIME = 5;
    private static final long MIN_DISTANCE = 1;

    private boolean btn_haveBeenClicked = false;
    private boolean tBtn_coverPassedHaveBeenClicked = false;
    private GoogleApiClient googleApiClient = null;

    private GoogleMap mMap;
    private ArrayList<LatLng> pointsForField = new ArrayList<>();
    private ArrayList<LatLng> pointsForLine = new ArrayList<>();
    private ArrayList<LatLng> pointsForAntennaLocation = new ArrayList<>();
    private Context context = null;
    private Controller controller = new Controller();

    private TextView mSpeed, mAccuracy, labelAboveToggleBtn;
    private RelativeLayout relativeLayoutWholeArrowForUserLocation, relativeLayoutForNavigationBar;
    private ImageView ivRightMark, ivLeftMark, ivCenterMark, ivTouchMainLineCalculation;
    private ToggleButton mainStartBtn, coverRouteTBtn, imageToggleButtonForActivationTouchListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        ImageView imageButtonForChangeMapTerrain = findViewById(R.id.bt_map_terrain_change);

        imageToggleButtonForActivationTouchListener = findViewById(R.id.bt_change_range_meter);
        relativeLayoutWholeArrowForUserLocation = findViewById(R.id.rl_user_arrow_image);
        labelAboveToggleBtn = findViewById(R.id.tv_label_for_toggle_button); //Initialize view to change it accordingly to mode
        mSpeed = findViewById(R.id.tv_speed_of_user); //Initialize view for MapsUtilities.getSpecsForStatusBar
        mAccuracy = findViewById(R.id.tv_accuracy_of_gps); //Initialize view for MapsUtilities.getSpecsForStatusBar
        mainStartBtn = findViewById(R.id.start_calculations); //Initialize view to make it invisible accordingly to mode
        coverRouteTBtn = findViewById(R.id.tBtn_cover_passed_places);
        relativeLayoutForNavigationBar = findViewById(R.id.rl_nav_bar);
        ivRightMark = findViewById(R.id.iv_right_way);
        ivLeftMark = findViewById(R.id.iv_left_way);
        ivCenterMark = findViewById(R.id.iv_center_way);
        ivTouchMainLineCalculation = findViewById(R.id.iv_touch_main_line_calculation);

        context = getApplicationContext(); //Set GetApplicationContext to use it all over the class

        createGoogleApiClient();

        MapsUtilities.counterToCheckIfModeChanged(labelAboveToggleBtn, mainStartBtn, coverRouteTBtn, relativeLayoutForNavigationBar, imageToggleButtonForActivationTouchListener, ivTouchMainLineCalculation);

        //Call the DialogChangeTerrain /layout to set terrain on map
        imageButtonForChangeMapTerrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapsUtilities.showAlertDialogRadio(getFragmentManager());//Set listener on button to transfer data to database
            }
        });

        ivTouchMainLineCalculation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controller.setProgramStatus(Controller.MODE_2_CREATE_LINE); //Re-set mode to 'Create Line' before execute the algorithm

                if (FieldFunctionsUtilities.algorithmForTouchMainLine(controller.getMarkerPosition(), context)) {
                    MapsUtilities.showAlertDialog(getFragmentManager());//Set listener on button to transfer data to database
                }
            }
        });

        //Set listener on button to start store LatLng on array
        imageToggleButtonForActivationTouchListener.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    controller.setTouchLineListener(true); //Set it true to make available feature to add points
                    controller.setProgramStatus(Controller.MODE_0_TOUCH_LISTENER); //Set mode to 'touch listener' to show the right buttons on toolbar
                    ivTouchMainLineCalculation.setVisibility(View.GONE);
                    Toast.makeText(context, "Hold on map to add the main line, through two points", Toast.LENGTH_SHORT).show();
                }else{
                    //TODO: When disable touch feature remove the line and values of main line to reset correctly the mode
                    controller.setProgramStatus(Controller.MODE_2_CREATE_LINE); //Re-set mode to 'Create Line' to show the right buttons on toolbar to
                    controller.setTouchLineListener(false); //Set it false to make disable feature to add points
                    Toast.makeText(context, "Touch line feature, disabled. Values for main line have been reset", Toast.LENGTH_SHORT).show();
                    if(controller.getMarkerPosition() != null){
                        controller.getMarkerPosition().clear(); //Reset Values of main line
                    }
                    MapsUtilities.recreateFieldWithMultiPolyline(mMap);
                }
            }
        });

        //Set listener on button to start store LatLng on array
        mainStartBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    Log.d(TAG, controller.getProgramStatus());
                    mainStartBtn.setTextColor(Color.parseColor("#a90404"));
                    if(controller.getProgramStatus().equals(Controller.MODE_1_RECORD_FIELD)){
                        Toast.makeText(context, "Start saving LatLng for field border", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(context, "Start saving LatLng for main line", Toast.LENGTH_SHORT).show();
                    }
                    btn_haveBeenClicked = true;
                    mainStartBtn.setClickable(false); //Unable to press it again until run the below command
                    MapsUtilities.counterToCheckIfArrayListIsEmpty(mainStartBtn);
                }else{
                    if(controller.getArrayListForField()!=null){
                        mainStartBtn.setTextColor(Color.WHITE);
                        Toast.makeText(context, "Stop saving LatLng", Toast.LENGTH_SHORT).show();
                        btn_haveBeenClicked = false;

                        MapsUtilities.showAlertDialog(getFragmentManager());//Set listener on button to transfer data to database
                    }
                }
            }
        });
        coverRouteTBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                tBtn_coverPassedHaveBeenClicked = isChecked;
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap; //App crash if it miss it
        controller.setGoogleMap(mMap);
        try{
            //Customise the styling of the base map using a JSON object defines in a raw resource file
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));
            if(!success){
                Log.e(TAG, "Style parsing failed.");
            }
        }catch (Resources.NotFoundException e){
            Log.e(TAG, "Can't find style. Error: ", e);
        }
        MapsUtilities.checkLocationPermission(context);
        PermissionUtilities.enableLoc(googleApiClient,this);

        mMap.setMyLocationEnabled(false);
//        mMap.getUiSettings().setZoomControlsEnabled(true);
//
//        mMap.setPadding(0,0,0, 100);
//        mMap.getUiSettings().setZoomGesturesEnabled(false);  //TODO: After finishing branch remove comments
//        mMap.getUiSettings().setScrollGesturesEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        checkToGetDataFromAnotherActivity(mainStartBtn, coverRouteTBtn);

        //Listener for touchLong to add 2 spots for Main line
        MapsUtilities.listenerForTouchAddOfMainLine();
        //Listener for click on a marker
        MapsUtilities.listenerForClickOnMarkers();
    }

    @Override
    public void onLocationChanged(Location location) {
        MapsUtilities.counterToCheckIfUserStandStill(mSpeed,mAccuracy,context); //Set the counter so i can reset speed/accuracy meter to 0 when it need it

        LatLng latLngOfCurrentTime = new LatLng(location.getLatitude(), location.getLongitude()); //Make location to LatLng
        controller.setCurrentLocation(latLngOfCurrentTime);
        float speedOfUser = location.getSpeed(); //Get speed of user
        float accuracyOfGps = location.getAccuracy(); //Get accuracy of user
        MapsUtilities.getSpecsForStatusBar(speedOfUser, accuracyOfGps, mSpeed, mAccuracy, context); // Show speed and accuracy of GPS up-right on map

        float userLocationBearing = location.getBearing(); //Get bearing so i can use it to follow the user with the right direction

        //This variable take latLngOfCurrentTime and see if user change pointOfCenter for navigation purpose.
        //If user doesn't change something to settingsActivity. App take as center the original center of map
        LatLng latLngForNavigationPurpose = FieldFunctionsUtilities.algorithmForDifferentCenterPoint(latLngOfCurrentTime, userLocationBearing);

        // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLngOfCurrentTime)                                                  // Sets the center of the map to Mountain View
                .zoom(MapsUtilities.changeZoomOfCameraBasedOnMode())                          // Sets the zoom
                .bearing(MapsUtilities.changeBearingOfCameraBasedOnMode(userLocationBearing)) // Sets the orientation of the camera to east
                .tilt(MapsUtilities.changeTiltOfCameraBasedOnMode())                          // Sets the tilt of the camera to 30 degrees
                .build();                                                                     // Creates a CameraPosition from the builder

        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition); //Set camera
        mMap.animateCamera(cameraUpdate); //Animate camera

        //Necessary converting from float (userLocationBearing) to double (convertedBearing) so it will be pass on the next functions
        Double convertedBearing = Double.parseDouble(String.valueOf(userLocationBearing));
        Log.d(TAG, String.valueOf(convertedBearing));

        //Use it to locate when user come close to polyline !!!
        if(controller.getProgramStatus().equals(Controller.MODE_3_DRIVING)){
            MapsUtilities.recreateFieldWithMultiPolyline(mMap); //Re draw the map to remove previous spots
            controller.setAntennaLocationForCircle(latLngForNavigationPurpose);
            if(pointsForAntennaLocation.size()>1){
                pointsForAntennaLocation.remove(0);
                MapsUtilities.recreateFieldWithMultiPolyline(mMap);
            }
            MapsUtilities.placeSpotOfAntenna(latLngForNavigationPurpose, mMap);
            //It has the job to not rotate whole arrow based on rotation because camera mode change
            MapsUtilities.changeRotationOnUserLocationArrow(relativeLayoutWholeArrowForUserLocation, (float) 0 );
            //Takes as input the 'latLngForNavigationPurpose' variable to use it for navigation feature
            FieldFunctionsUtilities.generateTempLineAndNavigationAlgorithm(mMap, latLngForNavigationPurpose, convertedBearing);
            //Interact with backLight of NavigationBar
            MapsUtilities.turnOnOffLightForNavigationBarToSetCourse(ivRightMark, ivLeftMark, ivCenterMark, context);
            //Takes as input the 'latLngForNavigationPurpose' variable to use it for coverRoute feature
            MapsUtilities.createCoverRouteUserPass(latLngForNavigationPurpose, tBtn_coverPassedHaveBeenClicked);
            //Algorithm from FieldFunctionsUtilities to calculate the width for polyline on coverRoute
            FieldFunctionsUtilities.calculationOfWidthForCoverRoute(controller.getCurrentLocation());
            //TODO: Fix polyline passed issue
            if(controller.getArrayListOfPlacedPolyLines() != null) {
                for (int j = 0; j < controller.getArrayListOfPlacedPolyLines().size(); j++) {
                    MapsUtilities.placePassedPlace(controller.getArrayListOfPlacedPolyLines().get(j), controller.getGoogleMap());
                }
            }
        }else{
            MapsUtilities.changeRotationOnUserLocationArrow(relativeLayoutWholeArrowForUserLocation, userLocationBearing);//It has the job to rotate whole arrow based on user bearing
        }

        //Save every lat\lng on specific arrayList<Lat/lng>. Depend on which mode app is !!
        if(controller.getProgramStatus().equals(Controller.MODE_1_RECORD_FIELD)
                && btn_haveBeenClicked) {

            pointsForField.add(latLngOfCurrentTime);
            controller.setArrayListForField(pointsForField);
            MapsUtilities.placePolylineForRoute(pointsForField, mMap);
        }
        else if(controller.getProgramStatus().equals(Controller.MODE_2_CREATE_LINE)
                && btn_haveBeenClicked
                && FieldFunctionsUtilities.PointIsInRegion(latLngOfCurrentTime, controller.getArrayListForField())){

            pointsForLine.add(latLngOfCurrentTime);
            controller.setArrayListForLine(pointsForLine);
            MapsUtilities.placePolylineForRoute(pointsForLine, mMap);
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {}
    @Override
    public void onProviderEnabled(String s) {
        Log.i(TAG, "Provider has been enabled");
    }
    @Override
    public void onProviderDisabled(String s) {
        Log.i(TAG, "Provider has been disabled");
    }
    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Suspended connection to Google Api Client");
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Failed to connect to  Google Api Client - " + connectionResult.getErrorMessage());
        googleApiClient.reconnect();
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Connected to Google Api Client");
        googleApiClient.connect();
    }
    @Override
    public void onBackPressed() {
        //Back Btn do nothing !
        mMap.clear();
        controller.setProgramStatus(Controller.MODE_1_RECORD_FIELD); //Reset the mode. Need a lot more but start from here
        if(getIntent().getExtras() == null){
            startActivity(new Intent(MapsActivity.this, MainActivity.class));
        }else{
            super.onBackPressed();
        }
    }

    public void createGoogleApiClient(){
        MapsUtilities.checkLocationPermission(context);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        googleApiClient = new GoogleApiClient.Builder(this).
                enableAutoManage(this, this).
                addApi(LocationServices.API).
                addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).
                build();

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null; //Auto-generate method for function requestLocationUpdates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
    }

    public void checkToGetDataFromAnotherActivity(ToggleButton mainCalculationTBtn, ToggleButton coverPassedPlacesTBtn){
        // When load field from "load btn" draw the necessary lines to show it and
        if(getIntent().getExtras()!=null) {
            controller.setProgramStatus(Controller.MODE_3_DRIVING); //Set the Driving mode to use app

            MapsUtilities.recreateFieldWithMultiPolyline(mMap); //Draw the map to work
            //Show the mode and hide tBtn
            MapsUtilities.changeLabelAboutMode(labelAboveToggleBtn, mainCalculationTBtn, coverPassedPlacesTBtn, relativeLayoutForNavigationBar, imageToggleButtonForActivationTouchListener, ivTouchMainLineCalculation);
        }else{
            controller.setProgramStatus(Controller.MODE_1_RECORD_FIELD);
            Log.d("modes",Controller.MODE_1_RECORD_FIELD);
        }
    }
}