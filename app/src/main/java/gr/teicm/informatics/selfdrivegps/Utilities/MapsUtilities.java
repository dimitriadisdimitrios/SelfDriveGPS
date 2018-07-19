package gr.teicm.informatics.selfdrivegps.Utilities;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;

import static java.lang.Float.MAX_VALUE;
import static java.lang.Math.*;

public class MapsUtilities {
//    private static String TAG = "MapsUtilities";
    static Controller controller = new Controller();

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
        boolean latLngExist = false;
        for(int i=0; i<points.size(); i++){
            if(points.get(i)==latLng){
                latLngExist=true;
            }
        }
        return latLngExist;
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

    public static void changeLabelAboutMode(TextView label){
        String modeOfApp = controller.getProgramStatus();
        switch (modeOfApp){
            case Controller.MODE_0_RECORD_FIELD:
                label.setText(Controller.MODE_0_RECORD_FIELD);
                break;
            case Controller.MODE_1_CREAT_LINE:
                label.setText(Controller.MODE_1_CREAT_LINE);
                break;
            case Controller.MODE_2_DRIVING:
                label.setText(Controller.MODE_2_DRIVING);
                break;
        }
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

