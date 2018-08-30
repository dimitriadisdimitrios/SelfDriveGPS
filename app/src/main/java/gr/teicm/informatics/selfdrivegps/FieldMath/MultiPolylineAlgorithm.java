package gr.teicm.informatics.selfdrivegps.FieldMath;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import gr.teicm.informatics.selfdrivegps.Controller.Controller;
import gr.teicm.informatics.selfdrivegps.Utilities.FieldFunctionsUtilities;

public class MultiPolylineAlgorithm {
//    private static final String TAG = "MultiPolylineAlgorithm";
    private static Controller controller = new Controller();

    //Function which calculate through algorithm how many polyline fits left and right of given polyline inside of field
    public static void algorithmForCreatingPolylineInField(ArrayList<LatLng> mArray/*, double distanceBetweenLines*/){

        ArrayList<ArrayList<LatLng>> outerArrayListForMultiPolyline = new ArrayList<>();
        ArrayList<LatLng> innerArrayListForMultiPolyline = new ArrayList<>();

        double bearingForRightSide = (FieldFunctionsUtilities.calculateBearing(mArray.get(0), mArray.get(mArray.size()-1))) + 90; // Get Bearing for right side
        double bearingForLeftSide = (FieldFunctionsUtilities.calculateBearing(mArray.get(0), mArray.get(mArray.size()-1))) + 270; // Get Bearing for left side
        double mBearing = FieldFunctionsUtilities.calculateBearing(mArray.get(0), mArray.get(mArray.size()-1));

        //TODO: Revision of loopToMultiPolyLines function
        loopToMultiPolyLines(mArray, bearingForRightSide, mBearing, mArray.size()-1, innerArrayListForMultiPolyline, outerArrayListForMultiPolyline); //#1
        loopToMultiPolyLines(mArray, bearingForLeftSide, mBearing, mArray.size()-1, innerArrayListForMultiPolyline, outerArrayListForMultiPolyline);  //#1

        FieldFunctionsUtilities.checkIfEveryPolylineMatchToTheEndOfBorder(mArray,mBearing); //Function that fill the blank spots

        //Set on controller the value of outerArrayList for polyLines
        controller.setArrayListOfMultipliedPolyLines(outerArrayListForMultiPolyline);
    }

    //Loop for create LatLng for Multi-polyLines (#1)
    private static void loopToMultiPolyLines(ArrayList<LatLng> mArrayToCheck, double mBearingWithTheRightSide, double mBearing, int sizeOfmArray, ArrayList<LatLng> inner, ArrayList<ArrayList<LatLng>> outer){
        double distanceBetweenLines = controller.getMeterOfRange(); //Reset distanceBetweenLines for algorithm \ Distance between lines

        while(FieldFunctionsUtilities.checkIfNextPolylineIsInsideOfField(mArrayToCheck, mBearingWithTheRightSide, distanceBetweenLines)){ //#2

            for(int i=0; i<=sizeOfmArray; i++){
                LatLng pointBeforeAddedToInner = FieldFunctionsUtilities.calculateLocationFewMetersAhead(mArrayToCheck.get(i), mBearingWithTheRightSide, distanceBetweenLines);

                //Check if any point of Polyline is out of field
                if(FieldFunctionsUtilities.PointIsInRegion(pointBeforeAddedToInner, controller.getArrayListForField())){
                    inner.add(FieldFunctionsUtilities.calculateLocationFewMetersAhead(mArrayToCheck.get(i), mBearingWithTheRightSide, distanceBetweenLines));
                }
            }
            FieldFunctionsUtilities.checkIfEveryPolylineMatchToTheEndOfBorder(inner,mBearing);

            ArrayList<LatLng> myTemp = new ArrayList<>(inner);
            outer.add(myTemp);
            inner.clear();

            distanceBetweenLines += controller.getMeterOfRange();
        }
        outer.add(0, mArrayToCheck); //Ensure that inside of controller.getArrayListOfMultipliedPolyLines (outerArrayListForMultiPolyLine)
    }
}
