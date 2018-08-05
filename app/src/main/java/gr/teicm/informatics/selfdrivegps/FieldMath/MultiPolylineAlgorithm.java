package gr.teicm.informatics.selfdrivegps.FieldMath;


import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import gr.teicm.informatics.selfdrivegps.Utilities.Controller;

public class MultiPolylineAlgorithm {
//    private static final String TAG = "MultiPolylineAlgorithm";
    private static Controller controller = new Controller();

    //Function which calculate through algorithm how many polyline fits left and right of given polyline inside of field
    public static void algorithmForCreatingPolylineInField(ArrayList<LatLng> mArray/*, double distanceBetweenLines*/){
        double bearingForRightSide = (FieldBorder.calculateBearing(mArray.get(0), mArray.get(mArray.size()-1))) + 90; // Get Bearing for right side
        double bearingForLeftSide = (FieldBorder.calculateBearing(mArray.get(0), mArray.get(mArray.size()-1))) + 270; // Get Bearing for left side
        double mBearing = FieldBorder.calculateBearing(mArray.get(0), mArray.get(mArray.size()-1));

        ArrayList<ArrayList<LatLng>> outerArrayListForMultiPolyline = new ArrayList<>();
        ArrayList<LatLng> innerArrayListForMultiPolyline = new ArrayList<>();

        loopToMultiPolyLines(mArray, bearingForRightSide, mBearing, mArray.size()-1, innerArrayListForMultiPolyline, outerArrayListForMultiPolyline); //#1
        loopToMultiPolyLines(mArray, bearingForLeftSide, mBearing, mArray.size()-1, innerArrayListForMultiPolyline, outerArrayListForMultiPolyline);  //#1

        checkIfEveryPolylineMatchToTheEndOfBorder(mArray, mArray.get(0),mBearing+180,false);
        checkIfEveryPolylineMatchToTheEndOfBorder(mArray, mArray.get(mArray.size()-1),mBearing,true);

        controller.setArrayListForLineTest(outerArrayListForMultiPolyline);
    }

    //Loop for create LatLng for Multi-polyLines (#1)
    private static void loopToMultiPolyLines(ArrayList<LatLng> mArrayToCheck, double mBearingWithTheRightSide, double mBearing, int sizeOfmArray, ArrayList<LatLng> inner, ArrayList<ArrayList<LatLng>> outer){
        double distanceBetweenLines = controller.getMeterOfRange(); //Reset distanceBetweenLines for algorithm \ Distance between lines

        while(checkIfNextPolylineIsInsideOfField(mArrayToCheck, mBearingWithTheRightSide, distanceBetweenLines)){ //#2

            for(int i=0; i<=sizeOfmArray; i++){
                LatLng pointBeforeAddedToInner = FieldBorder.calculateLocationFewMetersAhead(mArrayToCheck.get(i), mBearingWithTheRightSide, distanceBetweenLines);

                //Check if any point of Polyline is out of field
                if(FieldBorder.PointIsInRegion(pointBeforeAddedToInner, controller.getArrayListForField())){
                    inner.add(FieldBorder.calculateLocationFewMetersAhead(mArrayToCheck.get(i), mBearingWithTheRightSide, distanceBetweenLines));
                }
            }
            checkIfEveryPolylineMatchToTheEndOfBorder(inner, inner.get(0), mBearing+180, false);
            checkIfEveryPolylineMatchToTheEndOfBorder(inner, inner.get(inner.size()-1), mBearing, true);

            ArrayList<LatLng> myTemp = new ArrayList<>(inner);
            outer.add(myTemp);
            inner.clear();

            distanceBetweenLines += controller.getMeterOfRange();
        }
    }

    //Take 1 ArrayList<LatLng> and finds if the point(size/2) belongs to field (#2)
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

    //Check every arrayList if has place to add more points to fill the space
    private static void checkIfEveryPolylineMatchToTheEndOfBorder(ArrayList<LatLng> baseArrayListToAddExtraLatLng, LatLng latLngToCheck, double bearingOfPolyline, Boolean isTheEndOfArray){
        LatLng pointOfmArrayToCheck = FieldBorder.calculateLocationFewMetersAhead(latLngToCheck, bearingOfPolyline, 1);

        while(FieldBorder.PointIsInRegion(pointOfmArrayToCheck, controller.getArrayListForField())){
            if(isTheEndOfArray){
                baseArrayListToAddExtraLatLng.add(pointOfmArrayToCheck);
            }else{
                baseArrayListToAddExtraLatLng.add(0, pointOfmArrayToCheck);
            }
            pointOfmArrayToCheck = FieldBorder.calculateLocationFewMetersAhead(pointOfmArrayToCheck, bearingOfPolyline, 1);
        }
    }
}
