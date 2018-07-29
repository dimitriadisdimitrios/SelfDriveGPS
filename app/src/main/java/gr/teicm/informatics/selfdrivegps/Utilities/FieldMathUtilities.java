package gr.teicm.informatics.selfdrivegps.Utilities;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;

import static java.lang.Float.MAX_VALUE;
import static java.lang.Math.PI;
import static java.lang.Math.asin;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class FieldMathUtilities {
    private static String TAG = "FieldMathUtilities";
    private static Controller controller = new Controller();

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

    //Check if given point already exist inside on Array
    public static boolean checkIfLatLngExist(LatLng latLng, ArrayList<LatLng> points){
        boolean latLngExist = true;
        for(int i=0; i<points.size(); i++){
            if(points.get(i)==latLng){
                latLngExist=false;
            }
        }
        return latLngExist;
    }

    //Function to know if user is in polygon or not
    public static boolean PointIsInRegion(LatLng mLatLng, ArrayList<LatLng> thePath) {
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
            if (RayCrossesSegment(mLatLng, a, b)) {
                crossings++;
            }
        }
        return (crossings % 2 == 1); // odd number of crossings?
    }

    //Function which calculate through algorithm how many polyline fits left and right of given polyline inside of field
    public static void algorithmForCreatingPolylineInField(ArrayList<LatLng> mArray/*, double distanceBetweenLines*/){

        int sizeOfArray = mArray.size()-1;
        double bearingForRightSide = (calculateBearing(mArray.get(0), mArray.get(sizeOfArray))) + 90; // Get Bearing for right side
        double bearingForLeftSide = (calculateBearing(mArray.get(0), mArray.get(sizeOfArray))) + 270; // Get Bearing for left side

        ArrayList<ArrayList<LatLng>> outerArrayListForMultiPolyline = new ArrayList<>();
        ArrayList<LatLng> innerArrayListForMultiPolyline = new ArrayList<>();

        loopToMultiPolyLines(mArray, bearingForRightSide, sizeOfArray, innerArrayListForMultiPolyline, outerArrayListForMultiPolyline);
        loopToMultiPolyLines(mArray, bearingForLeftSide, sizeOfArray, innerArrayListForMultiPolyline, outerArrayListForMultiPolyline);

        controller.setArrayListForLineTest(outerArrayListForMultiPolyline);
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

    //Algorithm which find the point (x meter away with accordingly bearing)
    private static LatLng calculateLocationFewMetersAhead(LatLng sourceLatLng, double mBearing, double mMeter){
        double distRadians = mMeter / (6372797.6); // earth radius in meters

        double lat1 = sourceLatLng.latitude * PI / 180;
        double lon1 = sourceLatLng.longitude * PI / 180;

        double lat2 = asin(sin(lat1) * cos(distRadians) + cos(lat1) * sin(distRadians) * cos(Math.toRadians(mBearing)));
        double lon2 = lon1 + atan2(sin(Math.toRadians(mBearing)) * sin(distRadians) * cos(lat1), cos(distRadians) - sin(lat1) * sin(lat2));

        double nLat = lat2 * 180 / PI;
        double nLon = lon2 * 180 / PI;
        return new LatLng(nLat, nLon);
    }

    //Take 1 ArrayList<LatLng> and finds if the point(size/2) belongs to field
    private static boolean checkIfNextPolylineIsInsideOfField(ArrayList<LatLng> givenArrayListToCheck, double mBearing, double mMeter){

        boolean resultForCheckingIfPointIsInsideOfField = false;

        //Check every spot (x meter away with specific bearing) and if found at least one inside (stops) and return true
        for(int i=0; i<givenArrayListToCheck.size(); i++){
            LatLng tempSpot = calculateLocationFewMetersAhead(givenArrayListToCheck.get(i), mBearing, mMeter);
            if(PointIsInRegion(tempSpot, controller.getArrayListForField())){
                resultForCheckingIfPointIsInsideOfField = true;
                i = givenArrayListToCheck.size();
            }
        }
        return resultForCheckingIfPointIsInsideOfField;
    }

    //Take 2 points and find their bearing
    private static double calculateBearing(LatLng startLatLng, LatLng endLatLng){
        Double startLat = startLatLng.latitude;
        Double startLng = startLatLng.longitude;
        Double endLat = endLatLng.latitude;
        Double endLng = endLatLng.longitude;

        double longitude1 = startLng;
        double longitude2 = endLng;
        double latitude1 = Math.toRadians(startLat);
        double latitude2 = Math.toRadians(endLat);
        double longDiff= Math.toRadians(longitude2-longitude1);
        double y= Math.sin(longDiff)*Math.cos(latitude2);
        double x=Math.cos(latitude1)*Math.sin(latitude2)-Math.sin(latitude1)*Math.cos(latitude2)*Math.cos(longDiff);

        return (Math.toDegrees(Math.atan2(y, x))+360)%360;
    }

    //Loop for create LatLng for Multi-polyLines
    private static void loopToMultiPolyLines(ArrayList<LatLng> array, double mBearing, int sizeOfArray, ArrayList<LatLng> inner, ArrayList<ArrayList<LatLng>> outer){
        double distanceBetweenLines = controller.getMeterOfRange(); //Reset distanceBetweenLines for algorithm \ Distance between lines
        while(checkIfNextPolylineIsInsideOfField(array, mBearing, distanceBetweenLines)){
            for(int i=0; i<=sizeOfArray; i++){
                inner.add(calculateLocationFewMetersAhead(array.get(i), mBearing, distanceBetweenLines));
            }
            ArrayList<LatLng> myTemp = new ArrayList<>(inner);
            outer.add(myTemp);
            inner.clear();

            distanceBetweenLines+=controller.getMeterOfRange();
            Log.d(TAG, "## "+ myTemp+ "&&");
        }
    }
}
