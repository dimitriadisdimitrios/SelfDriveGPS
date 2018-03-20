package gr.teicm.informatics.selfdrivegps;

import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener
{
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static final String TAG = "MapsActivity";
    public static final long MIN_TIME = 100;
    public static final long MIN_DISTANCE = 2;
    boolean btn_haveBeenClicked = false;

    GoogleApiClient googleApiClient = null;

    private GoogleMap mMap;
    private LocationManager locationManager;
    private ArrayList<LatLng> points = new ArrayList<>();
    private Context context = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        context = getApplicationContext();

        //TODO: Improve names of buttons on both classes
        //Set Button from layout
        Button mainStartBtn = (Button) findViewById(R.id.start_calculations);
        Button sendDataToFireBase = (Button) findViewById(R.id.fireBase_btn);

        //Checking if it needs different permission access
        checkLocationPermission();

        //TODO: Improve If-Else method with his variable. Poor method code development
        //Set listener on button to start store LatLng on array
        mainStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btn_haveBeenClicked)
                {
                    Toast.makeText(context, "Stop saving LatLng", Toast.LENGTH_SHORT).show();
                    btn_haveBeenClicked=false;
                }
                else
                {
                    Toast.makeText(context, "Start saving LatLng", Toast.LENGTH_SHORT).show();
                    btn_haveBeenClicked=true;
                }
            }
        });

        //Set listener on button to transfer data to database
        sendDataToFireBase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Array[] storeLatLng = new Array[points.size()];
                Intent openPopAndSendArrayList = new Intent(MapsActivity.this, Pop.class);
//                startActivity(new Intent(MapsActivity.this, Pop.class));
                openPopAndSendArrayList.putExtra("ArrayList", points);
                startActivity(openPopAndSendArrayList);
            }
        });

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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        checkLocationPermission();
        mMap.setMyLocationEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    //TODO: Fix polyLine not to attach with previous LatLng when DemoBTN pushed again
    public void placePolylineForRoute(ArrayList<LatLng> directionPoints) {

        PolylineOptions rectLine = new PolylineOptions().width(5).color(Color.GRAY);

        Polyline routePolyline = null;

        for (int i = 0; i < directionPoints.size(); i++)
        {
            rectLine.add(directionPoints.get(i));
        }
        //clear the old line
        if (routePolyline != null)
        {
            routePolyline.remove();
        }
        mMap.addPolyline(rectLine);
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
        mMap.animateCamera(cameraUpdate);

        if(btn_haveBeenClicked)
            points.add(latLng);

        //check if latLng save on ArrayList() -> points
        Log.i(TAG, "!!! Location is " + /*latLng */  points );

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

    //All Permissions i need for android 6.0 and above
    public void checkLocationPermission() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

}