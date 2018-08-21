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
import android.widget.ImageButton;
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
        implements OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MapsActivity";
    private static final long MIN_TIME = 5;
    private static final long MIN_DISTANCE = 1;

    private boolean btn_haveBeenClicked = false;
    private GoogleApiClient googleApiClient = null;

    private GoogleMap mMap;
    private ArrayList<LatLng> pointsForField = new ArrayList<>();
    private ArrayList<LatLng> pointsForLine = new ArrayList<>();
    private Context context = null;
    private Controller controller = new Controller();

    private TextView mSpeed, mAccuracy, labelAboveToggleBtn;
    private RelativeLayout relativeLayoutForNavigationBar, relativeLayoutWholeArrowForUserLocation;
    private ImageButton imageButtonForChangeRangeMeter;
    private ImageView rightCube, leftCube, midCube;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        final ToggleButton mainStartBtn = findViewById(R.id.start_calculations); //Initialize view to make it invisible accordingly to mode
        ImageButton imageButtonForChangeMapTerrain = findViewById(R.id.bt_map_terrain_change);

        imageButtonForChangeRangeMeter = findViewById(R.id.bt_change_range_meter);
        relativeLayoutForNavigationBar = findViewById(R.id.rl_navigation_bar);
        relativeLayoutWholeArrowForUserLocation = findViewById(R.id.rl_user_arrow_image);
        labelAboveToggleBtn = findViewById(R.id.tv_label_for_toggle_button); //Initialize view to change it accordingly to mode
        mSpeed = findViewById(R.id.tv_speed_of_user); //Initialize view for MapsUtilities.getSpecsForStatusBar
        mAccuracy = findViewById(R.id.tv_accuracy_of_gps); //Initialize view for MapsUtilities.getSpecsForStatusBar
        rightCube = findViewById(R.id.iv_right_green_cube);
        leftCube = findViewById(R.id.iv_left_green_cube);
        midCube = findViewById(R.id.iv_center_green_cube);

        context = getApplicationContext(); //Set GetApplicationContext to use it all over the class

        createGoogleApiClient();

        MapsUtilities.counterToCheckIfModeChanged(labelAboveToggleBtn, mainStartBtn, relativeLayoutForNavigationBar, imageButtonForChangeRangeMeter);

        //Call the DialogChangeTerrain /layout to set terrain on map
        imageButtonForChangeMapTerrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapsUtilities.showAlertDialogRadio(getFragmentManager());//Set listener on button to transfer data to database
            }
        });

        //Call the DialogMainFunction /layout to set FieldName and RangeMeter
        imageButtonForChangeRangeMeter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(controller.getProgramStatus().equals(Controller.MODE_3_DRIVING)){ // Secure the controller.gerArrayList != null
                    MapsUtilities.showAlertDialog(getFragmentManager());
                }else{
                    Toast.makeText(context,"You don't have draw any field !", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(context, "Start saving LatLng", Toast.LENGTH_SHORT).show();
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
//        mMap.getUiSettings().setZoomGesturesEnabled(false);  //TODO: After finishing branch remove comments
//        mMap.getUiSettings().setScrollGesturesEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        ToggleButton mainStartBtn = findViewById(R.id.start_calculations); //Initialize view to make it invisible accordingly to mode

        checkToGetDataFromAnotherActivity(mainStartBtn);
    }

    @Override
    public void onLocationChanged(Location location) {
        MapsUtilities.counterToCheckIfUserStandStill(mSpeed,mAccuracy,context); //Set the counter so i can reset speed/accuracy meter to 0 when it need it

        LatLng latLngOfCurrentTime = new LatLng(location.getLatitude(), location.getLongitude());
        float speedOfUser = location.getSpeed();
        float accuracyOfGps = location.getAccuracy();
        MapsUtilities.getSpecsForStatusBar(speedOfUser, accuracyOfGps, mSpeed, mAccuracy, context); // Show speed and accuracy of GPS up-right on map

        float userLocationBearing = location.getBearing(); //Get bearing so i can use it to follow the user with the right direction

        // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLngOfCurrentTime)                                                  // Sets the center of the map to Mountain View
                .zoom(MapsUtilities.changeZoomOfCameraBasedOnMode())                          // Sets the zoom
                .bearing(MapsUtilities.changeBearingOfCameraBasedOnMode(userLocationBearing)) // Sets the orientation of the camera to east
                .tilt(MapsUtilities.changeTiltOfCameraBasedOnMode())                          // Sets the tilt of the camera to 30 degrees
                .build();                                                                     // Creates a CameraPosition from the builder

        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        mMap.animateCamera(cameraUpdate);

        //Necessary converting from float (userLocationBearing) to double (convertedBearing) so it will be pass on the next functions
        Double convertedBearing = Double.parseDouble(String.valueOf(userLocationBearing));
        Log.d(TAG, String.valueOf(convertedBearing));

        //Use it to locate when user come close to polyline !!!
        if(controller.getProgramStatus().equals(Controller.MODE_3_DRIVING)){
            MapsUtilities.changeRotationOnUserLocationArrow(relativeLayoutWholeArrowForUserLocation, (float) 0 );
            FieldFunctionsUtilities.generateTempLineAndNavigationAlgorithm(mMap, latLngOfCurrentTime, convertedBearing);
            MapsUtilities.turnOnOffLightBehindNavigationBarToSetCourse(rightCube, leftCube, midCube); //Interact with backLight of NavigationBar
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

    public void checkToGetDataFromAnotherActivity(ToggleButton mainBtn){
        // When load field from "load btn" draw the necessary lines to show it and
        if(getIntent().getExtras()!=null) {
            controller.setProgramStatus(Controller.MODE_3_DRIVING); //Set the Driving mode to use app

            MapsUtilities.recreateFieldWithMultiPolyline(mMap); //Draw the map to work
            MapsUtilities.changeLabelAboutMode(labelAboveToggleBtn, mainBtn, relativeLayoutForNavigationBar, imageButtonForChangeRangeMeter); //Show the mode and hide tBtn
        }else{
            controller.setProgramStatus(Controller.MODE_1_RECORD_FIELD);
            Log.d("modes",Controller.MODE_1_RECORD_FIELD);
        }
    }
}