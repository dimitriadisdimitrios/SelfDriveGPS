package gr.teicm.informatics.selfdrivegps;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import gr.teicm.informatics.selfdrivegps.Utilities.Controller;
import gr.teicm.informatics.selfdrivegps.Utilities.FieldMathUtilities;
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
        labelAboveToggleBtn = findViewById(R.id.tv_label_for_toggle_button); //Initialize view to change it accordingly to mode
        mSpeed = findViewById(R.id.tv_speed_of_user); //Initialize view for MapsUtilities.getSpecsForStatusBar
        mAccuracy = findViewById(R.id.tv_accuracy_of_gps); //Initialize view for MapsUtilities.getSpecsForStatusBar
        context = getApplicationContext(); //Set GetApplicationContext to use it all over the class

        createGoogleApiClient();

        MapsUtilities.checkIfModeChanged(labelAboveToggleBtn, mainStartBtn);

        checkToGetDataFromAnotherActivity(mainStartBtn);

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
        MapsUtilities.checkLocationPermission(context);
        PermissionUtilities.enableLoc(googleApiClient,this);

        mMap = googleMap;
        mMap.setMyLocationEnabled(false);
//        mMap.getUiSettings().setZoomGesturesEnabled(false);  //TODO: After finishing branch remove comments
//        mMap.getUiSettings().setScrollGesturesEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if(getIntent().getExtras()!=null) {
            LatLng center = FieldMathUtilities.getPolygonCenterPoint(pointsForField);
            mMap.addMarker(new MarkerOptions().position(center));
            //TODO: Adapt function from MapsUtilities here or create a new one
//            for(int i=0;i<360;i++){
//                LatLng aCester = MapsUtilities.calculateLocationFewMetersAhead(center,i);
//                mMap.addMarker(new MarkerOptions().position(aCester).title("a"));
//            }
            LatLng aCester = FieldMathUtilities.calculateLocationFewMetersAhead(center,270,100);
            mMap.addMarker(new MarkerOptions().position(aCester).title("d270"));
        }else{
            controller.setProgramStatus(Controller.MODE_0_RECORD_FIELD);
            Log.d("modes",Controller.MODE_0_RECORD_FIELD);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        MapsUtilities.checkIfUserStandStill(mSpeed,mAccuracy,context); //To reset speed/accuracy meter to 0

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        float speedOfUser = location.getSpeed();
        float accuracyOfGps = location.getAccuracy();
        MapsUtilities.getSpecsForStatusBar(speedOfUser, accuracyOfGps, mSpeed, mAccuracy, context); // Show speed and accuracy of GPS up-right on map

        float mBearing = location.getBearing(); //Get bearing so i can use it to follow the user with the right direction

        // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)             // Sets the center of the map to Mountain View
                .zoom(17)                   // Sets the zoom
                .bearing(mBearing)          // Sets the orientation of the camera to east
                .tilt(90)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder

        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        mMap.animateCamera(cameraUpdate);
        controller.setLocationOfUser(latLng);
//        Log.d(TAG, String.valueOf(pointsForLine));

        //Save every lat\lng on specific arrayList<Lat/lng>. Depend on which mode app is !!
        if(controller.getProgramStatus().equals(Controller.MODE_0_RECORD_FIELD)
                && btn_haveBeenClicked) {

            pointsForField.add(latLng);
            controller.setArrayListForField(pointsForField);
            MapsUtilities.placePolylineForRoute(pointsForField, mMap);
        }
        else if(controller.getProgramStatus().equals(Controller.MODE_1_CREAT_LINE)
                && btn_haveBeenClicked
                && FieldMathUtilities.checkIfLatLngExist(latLng,pointsForLine)
                && FieldMathUtilities.PointIsInRegion(latLng, controller.getArrayListForField())){

            pointsForLine.add(latLng);
            controller.setArrayListForLine(pointsForLine);
            MapsUtilities.placePolylineForRoute(pointsForLine, mMap);
        }
//        Log.d(TAG, String.valueOf(pointsForLine));

        //TODO: Fix the error when app starts in the Region
//        if(getIntent().getExtras()!=null){ Log.d("Point in Region", String.valueOf(MapsUtilities.PointIsInRegion(latLng,controller.getPoints())));}
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

        if(getIntent().getExtras()!=null){ //Check if app start from Start or from load field
            MapsUtilities.placePolygonForRoute(pointsForField,mMap);
        }
    }
    @Override
    public void onBackPressed() {
        //TODO: Add code on back btn to test it... When finished remove it all
        FieldMathUtilities.algorithmForCreatingPolylineInField(controller.getArrayListForLine());

        int j=0;
        for(int i=0; i<controller.getArrayListForLineTest().size()-1; i++){
            MapsUtilities.placeMultiPolyline(controller.getArrayListForLineTest().get(i), mMap, j);
            j++;
        }
//        Log.d("pizza", "!!!" + controller.getArrayListForLineTest().get(0));
//        Log.d(TAG, "pizza" + controller.getArrayListForLineTest().get(5));
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
        if(getIntent().getExtras()!=null) {
            pointsForField = getIntent().getParcelableArrayListExtra("latLng"); //Fill pointsForFieldFromBase with Lat\Lng
            controller.setArrayListForField(pointsForField);

            controller.setProgramStatus(Controller.MODE_1_CREAT_LINE);
            MapsUtilities.changeLabelAboutMode(labelAboveToggleBtn, mainBtn);
        }
    }
}