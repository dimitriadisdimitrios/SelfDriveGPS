package gr.teicm.informatics.selfdrivegps;

import android.Manifest;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
    private ArrayList<LatLng> points; //added



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //Set FireBase Database
        final DatabaseReference myRef1 = FirebaseDatabase.getInstance().getReference();

        //Set Button from layout
        Button mainStartBtn = (Button) findViewById(R.id.start_calculations);
        Button sendDataToFirebase = (Button) findViewById(R.id.fireBase_btn);

        //Checking if it needs different permission access
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {    checkLocationPermission();  }

        //Set arrayList for LatLng
        points = new ArrayList();

        //Todo: Improve If-Else methdod with his variable. Poor method code development
        //Set listener on button to start store LatLng on array
        mainStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btn_haveBeenClicked)
                    btn_haveBeenClicked=false;
                else
                    btn_haveBeenClicked=true;
            }
        });

        //Set listener on button to transfer data to database
        sendDataToFirebase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myRef1.setValue(points);
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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 600, 10, this);
    }

//    TODO: See where i need createLocationRequest Function
//    protected void createLocationRequest() {
//        mLocationRequest = new LocationRequest();
//        mLocationRequest.setInterval(INTERVAL);
//        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //checking again about sdk
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                mMap.setMyLocationEnabled(true);
            }
        }
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

//  TODO: I must find a way to make more simply the function checkLocationPerimission
    public boolean checkLocationPermission()
    {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            } else
            {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else
        {
            return true;
        }
    }

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
//        Location mLastLocation;
        Log.i(TAG, "Connected to Google Api Client");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            // TODO: Consider calling
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Suspended connection to Google Api Client");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            // TODO: Consider calling
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Failed to connect to  Google Api Client - " + connectionResult.getErrorMessage());
        googleApiClient.reconnect();
    }
}