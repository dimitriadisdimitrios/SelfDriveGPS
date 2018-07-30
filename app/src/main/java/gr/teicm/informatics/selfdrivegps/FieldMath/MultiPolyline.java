package gr.teicm.informatics.selfdrivegps.FieldMath;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import gr.teicm.informatics.selfdrivegps.Utilities.Controller;

public class MultiPolyline {
    private static String TAG = "MultiPolyline";
    private static Controller controller = new Controller();

    //Function which calculate through algorithm how many polyline fits left and right of given polyline inside of field
    public static void algorithmForCreatingPolylineInField(ArrayList<LatLng> mArray/*, double distanceBetweenLines*/){

        int sizeOfArray = mArray.size()-1;
        double bearingForRightSide = (FieldBorder.calculateBearing(mArray.get(0), mArray.get(sizeOfArray))) + 90; // Get Bearing for right side
        double bearingForLeftSide = (FieldBorder.calculateBearing(mArray.get(0), mArray.get(sizeOfArray))) + 270; // Get Bearing for left side

        ArrayList<ArrayList<LatLng>> outerArrayListForMultiPolyline = new ArrayList<>();
        ArrayList<LatLng> innerArrayListForMultiPolyline = new ArrayList<>();

        loopToMultiPolyLines(mArray, bearingForRightSide, sizeOfArray, innerArrayListForMultiPolyline, outerArrayListForMultiPolyline);
        loopToMultiPolyLines(mArray, bearingForLeftSide, sizeOfArray, innerArrayListForMultiPolyline, outerArrayListForMultiPolyline);

        controller.setArrayListForLineTest(outerArrayListForMultiPolyline);
    }

    //Loop for create LatLng for Multi-polyLines
    private static void loopToMultiPolyLines(ArrayList<LatLng> array, double mBearing, int sizeOfArray, ArrayList<LatLng> inner, ArrayList<ArrayList<LatLng>> outer){
        double distanceBetweenLines = controller.getMeterOfRange(); //Reset distanceBetweenLines for algorithm \ Distance between lines
        while(checkIfNextPolylineIsInsideOfField(array, mBearing, distanceBetweenLines)){
            for(int i=0; i<=sizeOfArray; i++){
                LatLng pointBeforeAddedToInner = FieldBorder.calculateLocationFewMetersAhead(array.get(i), mBearing, distanceBetweenLines);

                //Check if any point of Polyline is out of field
                if(FieldBorder.PointIsInRegion(pointBeforeAddedToInner, controller.getArrayListForField())){
                    inner.add(FieldBorder.calculateLocationFewMetersAhead(array.get(i), mBearing, distanceBetweenLines));
                }
            }
            ArrayList<LatLng> myTemp = new ArrayList<>(inner);
            outer.add(myTemp);
            inner.clear();

            distanceBetweenLines+=controller.getMeterOfRange();
            Log.d(TAG, "## "+ myTemp+ "&&");
        }
    }

    //Take 1 ArrayList<LatLng> and finds if the point(size/2) belongs to field
    private static boolean checkIfNextPolylineIsInsideOfField(ArrayList<LatLng> givenArrayListToCheck, double mBearing, double mMeter){

        boolean resultForCheckingIfPointIsInsideOfField = false;
        //Check every spot (x meter away with specific bearing) and if found at least one inside (stops) and return true
        for(int i=0; i<givenArrayListToCheck.size(); i++){
            LatLng tempSpot = FieldBorder.calculateLocationFewMetersAhead(givenArrayListToCheck.get(i), mBearing, mMeter);
            if(FieldBorder.PointIsInRegion(tempSpot, controller.getArrayListForField())){
                resultForCheckingIfPointIsInsideOfField = true;
                i = givenArrayListToCheck.size();
            }
        }
        return resultForCheckingIfPointIsInsideOfField;
    }
}
