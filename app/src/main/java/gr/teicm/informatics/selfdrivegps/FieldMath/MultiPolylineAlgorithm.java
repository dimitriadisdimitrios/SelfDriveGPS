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
        double mBearing = FieldFunctionsUtilities.calculateBearing(mArray.get(0), mArray.get(mArray.size()-1));

        loopToMultiPolyLines(mArray, mBearing, outerArrayListForMultiPolyline);

        FieldFunctionsUtilities.checkIfEveryPolylineMatchToTheEndOfBorder(mArray,mBearing); //Function that fill the blank spots

        //Set on controller the value of outerArrayList for polyLines
        controller.setArrayListOfMultipliedPolyLines(outerArrayListForMultiPolyline);
    }

    //Loop for create LatLng for Multi-polyLines (#1)
    private static void loopToMultiPolyLines(ArrayList<LatLng> mArrayToCheck, double mBearing, ArrayList<ArrayList<LatLng>> outer){

        double distanceBetweenLines = controller.getMeterOfRange(); //Reset distanceBetweenLines for algorithm \ Distance between lines
        double bearingForRightSide = (FieldFunctionsUtilities.calculateBearing(mArrayToCheck.get(0), mArrayToCheck.get(mArrayToCheck.size()-1))) + 90; // Get Bearing for right side
        double bearingForLeftSide = (FieldFunctionsUtilities.calculateBearing(mArrayToCheck.get(0), mArrayToCheck.get(mArrayToCheck.size()-1))) + 270; // Get Bearing for left side
        ArrayList<LatLng> innerArrayListForMultiRight = new ArrayList<>();
        ArrayList<LatLng> innerArrayListForMultiLeft = new ArrayList<>();
        LatLng pointBeforeAddedToInner;


        while(FieldFunctionsUtilities.checkIfNextPolylineIsInsideOfField(mArrayToCheck, bearingForRightSide, distanceBetweenLines)
                || FieldFunctionsUtilities.checkIfNextPolylineIsInsideOfField(mArrayToCheck, bearingForLeftSide, distanceBetweenLines)){

            for(int i=0; i<=mArrayToCheck.size()-1; i++){

                if(FieldFunctionsUtilities.checkIfNextPolylineIsInsideOfField(mArrayToCheck, bearingForRightSide, distanceBetweenLines)) {
                    pointBeforeAddedToInner = FieldFunctionsUtilities.calculateLocationFewMetersAhead(mArrayToCheck.get(i), bearingForRightSide, distanceBetweenLines);
                    //Check if any point of Polyline is out of field
                    if(FieldFunctionsUtilities.PointIsInRegion(pointBeforeAddedToInner, controller.getArrayListForField())){
                        innerArrayListForMultiRight.add(FieldFunctionsUtilities.calculateLocationFewMetersAhead(mArrayToCheck.get(i), bearingForRightSide, distanceBetweenLines));
                    }
                }
                if(FieldFunctionsUtilities.checkIfNextPolylineIsInsideOfField(mArrayToCheck, bearingForLeftSide, distanceBetweenLines)){
                    pointBeforeAddedToInner = FieldFunctionsUtilities.calculateLocationFewMetersAhead(mArrayToCheck.get(i), bearingForLeftSide, distanceBetweenLines);
                    //Check if any point of Polyline is out of field
                    if(FieldFunctionsUtilities.PointIsInRegion(pointBeforeAddedToInner, controller.getArrayListForField())){
                        innerArrayListForMultiLeft.add(FieldFunctionsUtilities.calculateLocationFewMetersAhead(mArrayToCheck.get(i), bearingForLeftSide, distanceBetweenLines));
                    }
                }
            }
            if(FieldFunctionsUtilities.checkIfNextPolylineIsInsideOfField(mArrayToCheck, bearingForRightSide, distanceBetweenLines)) {
                FieldFunctionsUtilities.checkIfEveryPolylineMatchToTheEndOfBorder(innerArrayListForMultiRight,mBearing);
                ArrayList<LatLng> myTemp = new ArrayList<>(innerArrayListForMultiRight);
                outer.add(myTemp);
                innerArrayListForMultiRight.clear();
            }
            if(FieldFunctionsUtilities.checkIfNextPolylineIsInsideOfField(mArrayToCheck, bearingForLeftSide, distanceBetweenLines)){
                FieldFunctionsUtilities.checkIfEveryPolylineMatchToTheEndOfBorder(innerArrayListForMultiLeft,mBearing);
                ArrayList<LatLng> myTemp = new ArrayList<>(innerArrayListForMultiLeft);
                outer.add(myTemp);
                innerArrayListForMultiLeft.clear();
            }

            distanceBetweenLines += controller.getMeterOfRange();
        }

        outer.add(0, mArrayToCheck); //Ensure that inside of controller.getArrayListOfMultipliedPolyLines (outerArrayListForMultiPolyLine)
    }
}
