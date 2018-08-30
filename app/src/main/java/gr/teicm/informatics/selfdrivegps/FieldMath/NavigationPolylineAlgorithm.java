package gr.teicm.informatics.selfdrivegps.FieldMath;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import gr.teicm.informatics.selfdrivegps.Controller.Controller;
import gr.teicm.informatics.selfdrivegps.Utilities.FieldFunctionsUtilities;

public class NavigationPolylineAlgorithm {
    //    private static final String TAG = "MultiPolylineAlgorithm";

    //Function which calculate through algorithm how many polyline fits left and right of given polyline inside of field
    public static ArrayList<ArrayList<LatLng>> algorithmForCreatingTwoInvisibleParallelPolylineForNavigation(ArrayList<LatLng> mArray){

        double bearingForRightSide = (FieldFunctionsUtilities.calculateBearing(mArray.get(0), mArray.get(mArray.size()-1))) + 90; // Get Bearing for right side
        double bearingForLeftSide = (FieldFunctionsUtilities.calculateBearing(mArray.get(0), mArray.get(mArray.size()-1))) + 270; // Get Bearing for left side
        double mBearing = FieldFunctionsUtilities.calculateBearing(mArray.get(0), mArray.get(mArray.size()-1));

        ArrayList<ArrayList<LatLng>> outerArrayListForMultiPolyline = new ArrayList<>();

        FieldFunctionsUtilities.checkIfEveryPolylineMatchToTheEndOfBorder(mArray, mBearing);

        outerArrayListForMultiPolyline.add(createTwoInvisiblePolyLinesForNavigation(mArray, bearingForLeftSide));
        outerArrayListForMultiPolyline.add(createTwoInvisiblePolyLinesForNavigation(mArray, bearingForRightSide));

        return outerArrayListForMultiPolyline;
        }

    //Take 1 ArrayList<LatLng> and finds if the point(size/2) belongs to field (#2)
    private static ArrayList<LatLng> createTwoInvisiblePolyLinesForNavigation(ArrayList<LatLng> mainPolyline, Double mainBearing){
        ArrayList<LatLng> parallelPolyLines = new ArrayList<>();
        for(LatLng mainPoint : mainPolyline){
            parallelPolyLines.add(FieldFunctionsUtilities.calculateLocationFewMetersAhead(mainPoint, mainBearing, Controller.MAIN_DISTANCE_FOR_INVISIBLE_POLYLINE));
        }
        return parallelPolyLines;
    }
}
