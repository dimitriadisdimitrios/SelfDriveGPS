package gr.teicm.informatics.selfdrivegps.Utilities;

import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;

public class Controller {
    private static ArrayList<LatLng> fieldArrayList, lineArrayList, lineFocus;
    private static ArrayList<ArrayList<LatLng>> lineTest;
    private static String idOfList, mStatus ="Record field selected";
    private static LatLng mLatLng;
    private static int mRange;

    public static final String MODE_0_RECORD_FIELD = "Record Field";
    public static final String MODE_1_CREATE_LINE = "Create Line";
    public static final String MODE_2_DRIVING = "Driving";

    public static final int MAIN_RADIUS_TO_RECOGNISE_POLYLINE = 1; // To meters

    //Setter/Getter for ArrayList<LatLng> which refer to Field (polygon)
    public void setArrayListForField(ArrayList<LatLng> points){
        fieldArrayList = points;
    }
    public ArrayList<LatLng> getArrayListForField(){
        return fieldArrayList;
    }
    //Setter/Getter for ArrayList<LatLng> which refer to Line inside of polygon
    public void setArrayListForLine(ArrayList<LatLng> linePoints){
        lineArrayList = linePoints;
    }
    public ArrayList<LatLng> getArrayListForLine(){
        return lineArrayList;
    }
    //Setter/Getter for ArrayList<LatLng> which refer to Line which user focus on navigation mode
    public void setArrayListForLineToFocus(ArrayList<LatLng> lineToFocus){
        lineFocus = lineToFocus;
    }
    public ArrayList<LatLng> getArrayListForLineToFocus(){
        return lineFocus;
    }

    //Setter/Getter for ArrayList<ArrayList<LatLng>> of multiplied polyLines
    public void setArrayListOfMultipliedPolyLines(ArrayList<ArrayList<LatLng>> linePoints){
        lineTest = linePoints;
    }
    public ArrayList<ArrayList<LatLng>> getArrayListOfMultipliedPolyLines(){
        return lineTest;
    }

    //Setter/Getter to change between "create field" and "create polyline"
    // Modes: "Record field", "Create Line", "Driving"
    public void setProgramStatus(String programStatus){
        mStatus = programStatus;
    }
    public String getProgramStatus(){
        return mStatus;
    }
    //Setter/Getter for get id from list of FireBase
    public void setIdOfListView(String id){
        idOfList = id;
    }
    public String getIdOfListView(){
        return idOfList;
    }

    //Setter/Getter to get current LatLng of user
    public void setLocationOfUser(LatLng latLng){
        mLatLng = latLng;
    }
    public LatLng getLocationOfUser() {
        return mLatLng;
    }

    //Setter/Getter to interact with range meter of settingActivity
    public void setMeterOfRange(int counter){
        mRange = counter;
    }
    public Integer getMeterOfRange(){
        return mRange;
    }
}