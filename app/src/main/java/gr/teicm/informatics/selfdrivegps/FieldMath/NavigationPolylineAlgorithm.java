package gr.teicm.informatics.selfdrivegps.FieldMath;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import gr.teicm.informatics.selfdrivegps.Controller.Controller;

public class NavigationPolylineAlgorithm {
    //    private static final String TAG = "MultiPolylineAlgorithm";
    private static Controller controller = new Controller();

    //Function which calculate through algorithm how many polyline fits left and right of given polyline inside of field
    public static ArrayList<ArrayList<LatLng>> algorithmForCreatingTwoInvisibleParallelPolylineForNavigation(ArrayList<LatLng> mArray){

        double bearingForRightSide = (AllFunctionAboutField.calculateBearing(mArray.get(0), mArray.get(mArray.size()-1))) + 90; // Get Bearing for right side
        double bearingForLeftSide = (AllFunctionAboutField.calculateBearing(mArray.get(0), mArray.get(mArray.size()-1))) + 270; // Get Bearing for left side
        double mBearing = AllFunctionAboutField.calculateBearing(mArray.get(0), mArray.get(mArray.size()-1));

        ArrayList<ArrayList<LatLng>> outerArrayListForMultiPolyline = new ArrayList<>();
//        ArrayList<LatLng> innerArrayListForMultiPolyline = new ArrayList<>();

        checkIfEveryPolylineMatchToTheEndOfBorder(mArray, mArray.get(0),mBearing+180,false);
        checkIfEveryPolylineMatchToTheEndOfBorder(mArray, mArray.get(mArray.size()-1), mBearing,true);

        outerArrayListForMultiPolyline.add(createTwoInvisiblePolylinesForNavation(mArray, bearingForLeftSide));
        outerArrayListForMultiPolyline.add(createTwoInvisiblePolylinesForNavation(mArray, bearingForRightSide));

        return outerArrayListForMultiPolyline;
        }

    //Check every arrayList if has place to add more points to fill the space
    private static void checkIfEveryPolylineMatchToTheEndOfBorder(ArrayList<LatLng> baseArrayListToAddExtraLatLng, LatLng latLngToCheck, double bearingOfPolyline, Boolean isTheEndOfArray){
        LatLng pointOfmArrayToCheck = AllFunctionAboutField.calculateLocationFewMetersAhead(latLngToCheck, bearingOfPolyline, 1);

        while(AllFunctionAboutField.PointIsInRegion(pointOfmArrayToCheck, controller.getArrayListForField())){
            if(isTheEndOfArray){
                baseArrayListToAddExtraLatLng.add(pointOfmArrayToCheck);
            }else{
                baseArrayListToAddExtraLatLng.add(0, pointOfmArrayToCheck);
            }
            pointOfmArrayToCheck = AllFunctionAboutField.calculateLocationFewMetersAhead(pointOfmArrayToCheck, bearingOfPolyline, 1);
        }
    }

    private static ArrayList<LatLng> createTwoInvisiblePolylinesForNavation(ArrayList<LatLng> mainPolyline, Double mainBearing){
        ArrayList<LatLng> parallelPolyLines = new ArrayList<>();
        for(LatLng mainPoint : mainPolyline){
            parallelPolyLines.add(AllFunctionAboutField.calculateLocationFewMetersAhead(mainPoint, mainBearing, Controller.MAIN_DISTANCE_FOR_INVISIBLE_POLYLINE));
        }
        return parallelPolyLines;

    }
}
