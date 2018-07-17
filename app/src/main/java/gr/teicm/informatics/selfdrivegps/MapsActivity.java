package gr.teicm.informatics.selfdrivegps;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
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
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import gr.teicm.informatics.selfdrivegps.Utilities.Controller;
import gr.teicm.informatics.selfdrivegps.Utilities.DialogFragmentUtility;
import gr.teicm.informatics.selfdrivegps.Utilities.MapsUtilities;
import gr.teicm.informatics.selfdrivegps.Utilities.PermissionUtilities;

public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MapsActivity";
    private static final long MIN_TIME = 5;
    private static final long MIN_DISTANCE = 1;

    private boolean btn_haveBeenClicked = false;
    private GoogleApiClient googleApiClient = null;

    private ArrayList<LatLng> mArray;
    private GoogleMap mMap;
    private ArrayList<LatLng> pointsForField = new ArrayList<>();
    private ArrayList<LatLng> pointsForLine = new ArrayList<>();
    private Context context = null;
    private Controller controller = new Controller();
    private Handler handler = new Handler();
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //Checking if it needs different permission access And create googleApiClient plus locationManager
        createGoogleApiClient();
        context = getApplicationContext();

        //Set Button from layout_maps
        final ToggleButton mainStartBtn =  findViewById(R.id.start_calculations);

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
                    showAlertDialog();//Set listener on button to transfer data to database
                    mMap.clear(); //Remove polyline from the record mode
                    placePolygonForRoute(controller.getArrayListForField()); //Get ArrayList<LatLng> to transfer polyline to polygon
//                    if(controller.getArrayListForField()!=null){
//                        placePolygonForRoute(controller.getArrayListForLine());
//                    }
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        checkLocationPermission();
        PermissionUtilities.enableLoc(googleApiClient,this);

        mMap = googleMap;
        mMap.setMyLocationEnabled(false);
//        mMap.getUiSettings().setZoomGesturesEnabled(false);  //TODO: After finishing branch remove comments
//        mMap.getUiSettings().setScrollGesturesEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if(getIntent().getExtras()!=null) {
            LatLng center = MapsUtilities.getPolygonCenterPoint(mArray);
            mMap.addMarker(new MarkerOptions().position(center));
            //TODO: Adapt function from MapsUtilities here or create a new one
//            for(int i=0;i<360;i++){
//                LatLng aCester = MapsUtilities.calculateLocationFewMetersAhead(center,i);
//                mMap.addMarker(new MarkerOptions().position(aCester).title("a"));
//            }
//            LatLng aCester = MapsUtilities.calculateLocationFewMetersAhead(center,0);
//            mMap.addMarker(new MarkerOptions().position(dCester).title("d270"));
        }else{
            controller.setProgramStatus(Controller.MODE_0_RECORD_FIELD);
            Log.d("modes",Controller.MODE_0_RECORD_FIELD);
        }
    }

    //TODO: See if i can reduce addition on ArrayList<LatLng>
    @Override
    public void onLocationChanged(Location location) {
        checkIfUserStandStill();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        float speedOfUser = location.getSpeed();
        float accuracyOfGps = location.getAccuracy();
        getSpecsForStatusBar(speedOfUser, accuracyOfGps); // Show speed and accuracy of GPS up-right on map

        //Get bearing so i can use it to follow the user with the right direction
        float mBearing = location.getBearing();
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
        Log.d(TAG, String.valueOf(pointsForLine));

        //TODO: create function on MapUtilities to make auto
        //Save every lat\lng on specific arrayList<Lat/lng>. Depend on which mode app is !!
        if(controller.getProgramStatus().equals(Controller.MODE_0_RECORD_FIELD)
                && btn_haveBeenClicked
                && !MapsUtilities.checkIfLatLngExist(latLng, pointsForField)) {
            pointsForField.add(latLng);
            controller.setArrayListForField(pointsForField);
//                Log.d(TAG, String.valueOf(points));
            placePolylineForRoute(pointsForField);
        }
        if(controller.getProgramStatus().equals(Controller.MODE_1_CREAT_LINE)
                && btn_haveBeenClicked
                && !MapsUtilities.checkIfLatLngExist(latLng,pointsForLine)){
            pointsForLine.add(latLng);
            controller.setArrayListForLine(pointsForLine);
//                Log.d(TAG, String.valueOf(points));
            placePolylineForRoute(pointsForLine);
        }

        //TODO: Fix the error when app starts in the Region
//        if(getIntent().getExtras()!=null){ Log.d("Point in Region", String.valueOf(MapsUtilities.PointIsInRegion(latLng,controller.getPoints())));}
    }

    public void placePolylineForRoute(ArrayList<LatLng> directionPoints) {
        PolylineOptions rectLine = new PolylineOptions()
                .width(5)
                .color(Color.RED);
        if(directionPoints!=null){
            for (int i = 0; i < directionPoints.size(); i++) {
                rectLine.add(directionPoints.get(i));
            }
        }
        mMap.addPolyline(rectLine);
    }
    public void placePolygonForRoute(ArrayList<LatLng> directionPoints){
        PolygonOptions polygonOptions = new PolygonOptions()
                .fillColor(Color.TRANSPARENT)
                .strokeColor(Color.GREEN)
                .strokeWidth(5);
        if(directionPoints!=null){
            for (int i = 0; i < directionPoints.size(); i++) {
                polygonOptions.add(directionPoints.get(i));
            }
        }
        mMap.addPolygon(polygonOptions);
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
            placePolygonForRoute(mArray);
        }
    }
    @Override
    public void onBackPressed() {
        //Back Btn do nothing !
        super.onBackPressed();
    }

    //All Permissions i need for android 6.0 and above
    public void checkLocationPermission() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Check passed");
            }
        }
    }

    public void createGoogleApiClient(){
        checkLocationPermission();
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

    public void showAlertDialog(){
        DialogFragmentUtility dialogFragmentUtility = new DialogFragmentUtility();
        dialogFragmentUtility.show(getFragmentManager(), "PopToSend");
        dialogFragmentUtility.setCancelable(false); //prevent dialog box from getting dismissed on back key
    }

    public void checkToGetDataFromAnotherActivity(ToggleButton mainBtn){
        mArray = getIntent().getParcelableArrayListExtra("latLng"); //Fill mArray with Lat\Lng
        //Make buttons invisible
        if(getIntent().getExtras()!=null) {
            String valueFromRetrieveDataActivityClass = getIntent().getExtras().getString("buttonStatus");
            if (valueFromRetrieveDataActivityClass!=null&& valueFromRetrieveDataActivityClass.equals("invisible")) {
                mainBtn.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void getSpecsForStatusBar(float speed, float accuracy){
        TextView mSpeed = findViewById(R.id.tv_speed_of_user);
        TextView mAccuracy = findViewById(R.id.tv_accuracy_of_gps);

        float kmH = (float) (speed *3.6); //Convert m/s to km/h

        mSpeed.setText(getString(R.string.speed_counter, kmH));
        mAccuracy.setText(getString(R.string.accuracy_of_gps, accuracy));
    }

    //Check if user moving. If it stay still the counter start to reset speed and accuracy
    public void checkIfUserStandStill(){
        handler.removeCallbacks(runnable);
        runnable = new Runnable() {
            @Override
            public void run() {
                getSpecsForStatusBar(0,0);
            }
        };
        handler.postDelayed(runnable, 1500);
    }
}