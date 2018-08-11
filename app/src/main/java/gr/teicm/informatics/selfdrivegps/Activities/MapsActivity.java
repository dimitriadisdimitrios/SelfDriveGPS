package gr.teicm.informatics.selfdrivegps.Activities;

import android.content.Context;
import android.content.res.Resources;
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
import android.widget.ProgressBar;
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

import gr.teicm.informatics.selfdrivegps.FieldMath.AllFunctionAboutField;
import gr.teicm.informatics.selfdrivegps.FieldMath.NavigationPolylineAlgorithm;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        ToggleButton mainStartBtn = findViewById(R.id.start_calculations); //Initialize view to make it invisible accordingly to mode
        ImageButton imageButtonForChangeMapTerrain = findViewById(R.id.bt_map_terrain_change);
        ImageButton imageButtonForChangeRangeMeter = findViewById(R.id.bt_change_range_meter);

        labelAboveToggleBtn = findViewById(R.id.tv_label_for_toggle_button); //Initialize view to change it accordingly to mode
        mSpeed = findViewById(R.id.tv_speed_of_user); //Initialize view for MapsUtilities.getSpecsForStatusBar
        mAccuracy = findViewById(R.id.tv_accuracy_of_gps); //Initialize view for MapsUtilities.getSpecsForStatusBar
        context = getApplicationContext(); //Set GetApplicationContext to use it all over the class

        createGoogleApiClient();

        MapsUtilities.checkIfModeChanged(labelAboveToggleBtn, mainStartBtn);

        //Call the DialogFragmentRadio /layout to set terrain on map
        imageButtonForChangeMapTerrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapsUtilities.showAlertDialogRadio(getFragmentManager());//Set listener on button to transfer data to database
            }
        });
        //Call the DialogFragment /layout to set FieldName and RangeMeter
        imageButtonForChangeRangeMeter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controller.setLastProgramStatus(controller.getProgramStatus());
                controller.setProgramStatus(Controller.MODE_2_CREATE_LINE);
                MapsUtilities.showAlertDialog(getFragmentManager());
            }
        });

        //Set listener on button to start store LatLng on array
        mainStartBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(context, "Start saving LatLng", Toast.LENGTH_SHORT).show();
                    btn_haveBeenClicked = true;

                } else {
                    Toast.makeText(context, "Stop saving LatLng", Toast.LENGTH_SHORT).show();
                    btn_haveBeenClicked = false;

                    MapsUtilities.showAlertDialog(getFragmentManager());//Set listener on button to transfer data to database

                    mMap.clear(); //Remove polyline from the record mode
                    MapsUtilities.placePolygonForRoute(controller.getArrayListForField(), mMap); //Get ArrayList<LatLng> to transfer polyline to polygon
                    if(controller.getArrayListForLine()!=null && !controller.getArrayListForLine().isEmpty()){
                        MapsUtilities.placePolylineForRoute(controller.getArrayListForLine(), mMap);
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

        if(getIntent().getExtras()!=null) {
            //TODO: Finish with navigationAlgorithm and then find a way to pop Line range meter to use it (uncomment it and remove the next 2 lines)
//            MapsUtilities.recreateFieldWithMultiPolyline(mMap);
        }else{
            controller.setProgramStatus(Controller.MODE_1_RECORD_FIELD);
            Log.d("modes",Controller.MODE_1_RECORD_FIELD);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        MapsUtilities.checkIfUserStandStill(mSpeed,mAccuracy,context); //To reset speed/accuracy meter to 0

        LatLng latLngOfCurrentTime = new LatLng(location.getLatitude(), location.getLongitude());
        float speedOfUser = location.getSpeed();
        float accuracyOfGps = location.getAccuracy();
        MapsUtilities.getSpecsForStatusBar(speedOfUser, accuracyOfGps, mSpeed, mAccuracy, context); // Show speed and accuracy of GPS up-right on map

        float mBearing = location.getBearing(); //Get bearing so i can use it to follow the user with the right direction

        // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLngOfCurrentTime)             // Sets the center of the map to Mountain View
                .zoom(20)                   // Sets the zoom
                .bearing(mBearing)          // Sets the orientation of the camera to east
                .tilt(90)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder

        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        mMap.animateCamera(cameraUpdate);

        //TODO: Use it to locate when user come close to polyline !!!
        if(getIntent().getExtras()!=null){
//            if (MapsUtilities.checkingInWhichPolylineUserEntered(latLngOfCurrentTime)){
//                Toast.makeText(this, "Entered in " + controller.getArrayListOfMultipliedPolyLines().indexOf(controller.getArrayListForLineToFocus()), Toast.LENGTH_SHORT).show();
//            }
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
                && AllFunctionAboutField.checkIfLatLngExist(latLngOfCurrentTime,pointsForLine)
                && AllFunctionAboutField.PointIsInRegion(latLngOfCurrentTime, controller.getArrayListForField())){

            pointsForLine.add(latLngOfCurrentTime);
            controller.setArrayListForLine(pointsForLine);
            MapsUtilities.placePolylineForRoute(pointsForLine, mMap);
        }
//        Log.d(TAG, String.valueOf(pointsForLine));
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
        //TODO: Add code on back btn to test it... When finished remove it all
        mMap.clear();
//        MapsUtilities.placePolygonForRoute(controller.getArrayListForField(), mMap);
//        MultiPolylineAlgorithm.algorithmForCreatingPolylineInField(controller.getArrayListForLine());
//        MapsUtilities.placePolylineForRoute(controller.getArrayListForLine(),mMap);
//        for(int i = 0; i<controller.getArrayListOfMultipliedPolyLines().size(); i++){
//            MapsUtilities.placePolylineForRoute(controller.getArrayListOfMultipliedPolyLines().get(i), mMap);
//        }
        ArrayList<ArrayList<LatLng>> parPolyline = NavigationPolylineAlgorithm.algorithmForCreatingTwoInvisibleParallelPolylineForNavigation(controller.getArrayListForLine());
        MapsUtilities.recreateFieldWithMultiPolyline(mMap);

        for(ArrayList<LatLng> temp : parPolyline){
            MapsUtilities.placePolylineParallel(temp, mMap);
        }
        //Back Btn do nothing !
//        super.onBackPressed();
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
            MapsUtilities.placePolygonForRoute(controller.getArrayListForField(),mMap);
            MapsUtilities.placePolylineForRoute(controller.getArrayListForLine(), mMap);

            controller.setProgramStatus(Controller.MODE_3_DRIVING);
            MapsUtilities.changeLabelAboutMode(labelAboveToggleBtn, mainBtn);
        }
    }
}