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
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import gr.teicm.informatics.selfdrivegps.R;

import static java.lang.Float.MAX_VALUE;
import static java.lang.Math.*;

public class MapsUtilities {
    private static String TAG = "MapsUtilities";
    private static Controller controller = new Controller();
    private static Handler handler = new Handler();
    private static Runnable runnableForModes, runnableForSpeed;

    //It find the center of polygon
    public static LatLng getPolygonCenterPoint(ArrayList<LatLng> polygonPointsList) {
        LatLng centerLatLng;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (int i = 0; i < polygonPointsList.size(); i++) {
            builder.include(polygonPointsList.get(i));
        }
        LatLngBounds bounds = builder.build();
        centerLatLng = bounds.getCenter();

        return centerLatLng;
    }

    public static boolean checkIfLatLngExist(LatLng latLng, ArrayList<LatLng> points){
        boolean latLngExist = true;
        for(int i=0; i<points.size(); i++){
            if(points.get(i)==latLng){
                latLngExist=false;
            }
        }
        return latLngExist;
    }

    public static void showAlertDialog(android.app.FragmentManager fragmentManager){
        DialogFragmentUtility dialogFragmentUtility = new DialogFragmentUtility();
        dialogFragmentUtility.show(fragmentManager, "PopToSend");
        dialogFragmentUtility.setCancelable(false); //prevent dialog box from getting dismissed on back key
    }

    //TODO: See when function used
    public static boolean hasPermissions(Context context, String... allPermissionNeeded) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && context != null && allPermissionNeeded != null)
            for (String permission : allPermissionNeeded)
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
                    return false;
        return true;
    }

    //All Permissions i need for android 6.0 and above
    public static void checkLocationPermission(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Check passed");
            }
        }
    }

    public static void changeLabelAboutMode(TextView label, ToggleButton startStopTBtn){
        String modeOfApp = controller.getProgramStatus();
        switch (modeOfApp){
            case Controller.MODE_0_RECORD_FIELD:
                label.setText(String.format("Mode: %s", Controller.MODE_0_RECORD_FIELD));
                break;
            case Controller.MODE_1_CREAT_LINE:
                label.setText(String.format("Mode: %s", Controller.MODE_1_CREAT_LINE));
                break;
            case Controller.MODE_2_DRIVING:
                label.setText(String.format("Mode: %s", Controller.MODE_2_DRIVING));
                startStopTBtn.setVisibility(View.INVISIBLE);
                break;
        }
    }

    public static void placePolylineForRoute(ArrayList<LatLng> directionPoints, GoogleMap googleMap) {
        PolylineOptions rectLine = new PolylineOptions()
                .width(5)
                .color(Color.RED);
        if(directionPoints!=null){
            for (int i = 0; i < directionPoints.size(); i++) {
                rectLine.add(directionPoints.get(i));
            }
        }
        googleMap.addPolyline(rectLine);
    }
    public static void placePolygonForRoute(ArrayList<LatLng> directionPoints, GoogleMap googleMap){
        PolygonOptions polygonOptions = new PolygonOptions()
                .fillColor(Color.TRANSPARENT)
                .strokeColor(Color.GREEN)
                .strokeWidth(5);
        if(directionPoints!=null){
            for (int i = 0; i < directionPoints.size(); i++) {
                polygonOptions.add(directionPoints.get(i));
            }
        }
        googleMap.addPolygon(polygonOptions);
    }

    public static void getSpecsForStatusBar(float speed, float accuracy, TextView mSpeed, TextView mAccuracy, Context context){
        float kmH = (float) (speed *3.6); //Convert m/s to km/h

        mSpeed.setText(context.getString(R.string.speed_counter, kmH));
        mAccuracy.setText(context.getString(R.string.accuracy_of_gps, accuracy));
    }

    //Check if user moving. If it stay still the counter start to reset speed and accuracy
    public static void checkIfUserStandStill(final TextView mSpeed, final TextView mAccuracy, final Context context){
        handler.removeCallbacks(runnableForSpeed);
        runnableForSpeed = new Runnable() {
            @Override
            public void run() {
                MapsUtilities.getSpecsForStatusBar(0,0, mSpeed, mAccuracy, context);
            }
        };
        handler.postDelayed(runnableForSpeed, 1500);
    }
    public static void checkIfModeChanged(final TextView textView, final ToggleButton toggleButton){
        runnableForModes = new Runnable() {
            @Override
            public void run() {
                changeLabelAboutMode(textView, toggleButton);
                handler.postDelayed(runnableForModes,1000);
            }
        };
        handler.postDelayed(runnableForModes, 1000);
    }

    //Function to know if user is in polygon or not
    public static boolean PointIsInRegion(LatLng mlatLng, ArrayList<LatLng> thePath)
    {
        int crossings = 0;
        int count = thePath.size();
        LatLng a,b;

        for (int i=0; i < count; i++) { // for each edge
            a = thePath.get(i);
            int j = i + 1;
            if (j >= count) {
                j = 0;
            }
            b = thePath.get(j);
            if (RayCrossesSegment(mlatLng, a, b)) {
                crossings++;
            }
        }
        return (crossings % 2 == 1); // odd number of crossings?
    }

    //Ray algorithm to calculate area of polygon
    private static boolean RayCrossesSegment(LatLng point, LatLng a, LatLng b) {
        double px = point.longitude;
        double py = point.latitude;
        double ax = a.longitude;
        double ay = a.latitude;
        double bx = b.longitude;
        double by = b.latitude;
        if (ay > by)
        {
            ax = b.longitude;
            ay = b.latitude;
            bx = a.longitude;
            by = a.latitude;
        }
        // alter longitude to cater for 180 degree crossings
        if (px < 0) { px += 360; }
        if (ax < 0) { ax += 360; }
        if (bx < 0) { bx += 360; }

        if (py == ay || py == by) py += 0.00000001;
        if ((py > by || py < ay) || (px > Math.max(ax, bx))) return false;
        if (px < Math.min(ax, bx)) return true;

        double red = (ax != bx) ? ((by - ay) / (bx - ax)) : MAX_VALUE;
        double blue = (ax != px) ? ((py - ay) / (px - ax)) : MAX_VALUE;
        Log.d(TAG, "blue >= red: " + (blue >= red));
        return (blue >= red);
    }

    public static LatLng calculateLocationFewMetersAhead(LatLng sourceLatLng, int mBearing, double mMeter){
//        double meters = 50;
        double distRadians = mMeter / (6372797.6); // earth radius in meters

        double lat1 = sourceLatLng.latitude * PI / 180;
        double lon1 = sourceLatLng.longitude * PI / 180;

        double lat2 = asin(sin(lat1) * cos(distRadians) + cos(lat1) * sin(distRadians) * cos(Math.toRadians(mBearing)));
        double lon2 = lon1 + atan2(sin(Math.toRadians(mBearing)) * sin(distRadians) * cos(lat1), cos(distRadians) - sin(lat1) * sin(lat2));

        double nLat = lat2 * 180 / PI;
        double nLon = lon2 * 180 / PI;
        return new LatLng(nLat, nLon);
    }
}

