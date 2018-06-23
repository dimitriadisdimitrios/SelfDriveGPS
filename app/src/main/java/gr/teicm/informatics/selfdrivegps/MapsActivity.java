package gr.teicm.informatics.selfdrivegps;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
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
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static final String TAG = "MapsActivity";
    public static final long MIN_TIME = 100;
    public static final long MIN_DISTANCE = 2;
    public String nameOfDataBaseKey;

    boolean btn_haveBeenClicked = false;
    GoogleApiClient googleApiClient = null;

    private ArrayList<LatLng> mArray;
    private GoogleMap mMap;
    private LocationManager locationManager;
    private ArrayList<LatLng> points = new ArrayList<>();
    private Context context = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //TODO: Fix first startup which the app crash because awaits the permission window
        //Checking if it needs different permission access And create googleApiClient plus locationManager
        checkLocationPermission();
        createGoogleApiClient();
        context = getApplicationContext();


        //Set Button from layout_maps
        final ToggleButton mainStartBtn = (ToggleButton) findViewById(R.id.start_calculations);
        final Button openPopUpWindow = (Button) findViewById(R.id.start_pop_btn);

        checkToGetDataFromAnotherActivity(mainStartBtn, openPopUpWindow);

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
                }
            }
        });

        //Set listener on button to transfer data to database
        openPopUpWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        checkLocationPermission();
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //Check if app start from Start or from load field
        if(getIntent().getExtras()!=null){
            placePolygonForRoute(mArray);
        }
    }

    //TODO: Fix polyLine not to attach with previous LatLng when DemoBTN pushed again
    //TODO: Check if ArrayList save the same Lat\Lng up to 1 time
    public void placePolylineForRoute(ArrayList<LatLng> directionPoints) {
        PolylineOptions rectLine = new PolylineOptions()
                .width(5)
                .color(Color.GREEN);

        if(directionPoints!=null){
            for (int i = 0; i < directionPoints.size(); i++) {
                rectLine.add(directionPoints.get(i));
            }
        }
        mMap.addPolyline(rectLine);
    }
    public void placePolygonForRoute(ArrayList<LatLng> directionPoints){
        PolygonOptions polygonOptions = new PolygonOptions()
                .fillColor(Color.GREEN)
                .strokeColor(Color.GREEN)
                .strokeWidth(2);
        if(directionPoints!=null){
            for (int i = 0; i < directionPoints.size(); i++) {
                polygonOptions.add(directionPoints.get(i));
            }
        }
        mMap.addPolygon(polygonOptions);
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        float speedOfUser = location.getSpeed();
        float accuracyOfGps = location.getAccuracy();
        getSpeedOfUser(speedOfUser);
        getGpsAccuracy(accuracyOfGps);

        //Get bearing so i can use it to follow the user with the right direction
        float mBearing = location.getBearing();
        // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)             // Sets the center of the map to Mountain View
                .zoom(12)                   // Sets the zoom
                .bearing(mBearing)          // Sets the orientation of the camera to east
                .tilt(90)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder

        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        mMap.animateCamera(cameraUpdate);

        if(btn_haveBeenClicked) {
            points.add(latLng);
        }

        placePolylineForRoute(points);
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
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Connected to Google Api Client");

        checkLocationPermission();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Suspended connection to Google Api Client");

        checkLocationPermission();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Failed to connect to  Google Api Client - " + connectionResult.getErrorMessage());

        googleApiClient.reconnect();
    }

    @Override
    public void onBackPressed() {
        //Back Btn do nothing !
//        super.onBackPressed();
    }

    //All Permissions i need for android 6.0 and above
    public void checkLocationPermission() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
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

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null; //Auto-generate method for function requestLocationUpdates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 600, 10, this);
    }

    public void showAlertDialog(){
        //Create mView to interAct with activity_pop
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MapsActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.activity_pop,null);
        mBuilder.setView(mView);

        //Set Button from layout_pop
        final EditText collectionOfLatLng = (EditText) mView.findViewById(R.id.pop_name_DB_ET);
        Button cancelPopUpWindow = (Button) mView.findViewById(R.id.cancel_sending_pop_btn);
        Button sendToFireBaseDataFromPop = (Button) mView.findViewById(R.id.send_data_to_fireBase_Btn);

        final AlertDialog dialog = mBuilder.create(); // Create dialog
        dialog.show(); // Show the dialog
        dialog.setCancelable(false); //prevent dialog box from getting dismissed on back key

        //Cancel Button listener
        cancelPopUpWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Toast.makeText(context,"Preparation for sending Canceled !", Toast.LENGTH_SHORT).show();
            }
        });

        //Send Button listener
        sendToFireBaseDataFromPop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get text from editBox
                nameOfDataBaseKey = collectionOfLatLng.getText().toString();

                //Connect FireBase Database so I will able to use it
                final DatabaseReference myRef1 = FirebaseDatabase.getInstance().getReference();


                //Check if ET is empty or not and if it isn't send data to fireBase with specific name key
                if(!nameOfDataBaseKey.matches("")) {
                    myRef1.child(nameOfDataBaseKey).setValue(points); //Create child with specific name which include LatLng
                    Toast.makeText(context, "LatLng have been added", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
                else {
                    Toast.makeText(context, "Name of Key is empty !", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void checkToGetDataFromAnotherActivity(ToggleButton mainBtn, Button openPopUp){
        try{
            //Fill mArray with Lat\Lng
            mArray = getIntent().getParcelableArrayListExtra("latLng");

            //Make buttons invisible
            String valueFromRetrieveDataActivityClass = getIntent().getExtras().getString("buttonStatus");
            if(valueFromRetrieveDataActivityClass.equals("invisible")){
                mainBtn.setVisibility(View.INVISIBLE);
                openPopUp.setVisibility(View.INVISIBLE);
            }
        }catch (NullPointerException e){
            Log.d(TAG, "Start to create plan");
        }
    }

    public void getSpeedOfUser(float speed){
        TextView mSpeed = (TextView) findViewById(R.id.tv_speed_of_user);

        //Convert m/s to km/h
        float kmH = (float) (speed *3.6);
        String result = String.format("%.1f", kmH);
        mSpeed.setText(result+" km/h ");
    }

    public void getGpsAccuracy(float accuracy){
        TextView mAccuracy = (TextView) findViewById(R.id.tv_accuracy_of_gps);
        mAccuracy.setText(accuracy+" m ");
    }

//    public void createGeofenceObject(String id){
//        Geofence geofence = new Geofence.Builder()
//                .setRequestId(id)
//                .setCircularRegion(quest.getLatitude(), quest.getLongitude(), 50)
//                .setExpirationDuration(Geofence.NEVER_EXPIRE)
//                .setNotificationResponsiveness(1000)
//                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
//                .build();
//
//        GeofencingRequest geofencingRequest = new GeofencingRequest.Builder()
//                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
//                .addGeofence(geofence)
//                .build();
//
//    }
}