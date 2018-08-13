package gr.teicm.informatics.selfdrivegps.Controller;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;

public class Controller {
    private static ArrayList<LatLng> fieldArrayList, lineArrayList, lineFocus;
    private static ArrayList<ArrayList<LatLng>> lineTest;
    private static String idOfList, mStatus ="Record field selected";
    private static Integer mRange;
    private static GoogleMap gMap;
    private static Boolean mNameExist;
    private static Double mBearing;

    public static final String MODE_1_RECORD_FIELD = "Record Field";
    public static final String MODE_2_CREATE_LINE = "Create Line";
    public static final String MODE_3_DRIVING = "Driving";

    public static final double MAIN_RADIUS_TO_RECOGNISE_MAIN_POLYLINE = 3; // To meters //it works for 2.5
    public static final double MAIN_RADIUS_TO_RECOGNISE_SECONDARY_POLYLINE = 1; // To meters
    public static final double MAIN_DISTANCE_FOR_INVISIBLE_POLYLINE = 2;

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

    //Setter/Getter to interact with range meter of settingActivity
    public void setMeterOfRange(Integer counter){
        mRange = counter;
    }
    public Integer getMeterOfRange(){
        return mRange;
    }

    //Setter/Getter to set GoogleMap to work on DialogFragment
    public void setGoogleMap(GoogleMap map){
        gMap = map;
    }
    public GoogleMap getGoogleMap(){
        return gMap;
    }

    //Setter/Getter to interact with range meter of settingActivity
    public void setIfFoundMatchOnFireBase(Boolean nameToMatch){
        mNameExist = nameToMatch;
    }
    public Boolean getIfFoundMatchOnFireBase(){
        return mNameExist;
    }

    //Setter/Getter for bearing to use it for the navigationAlgorithm
    public void setBearingForNavigationPurpose(Double mainBearing){
        mBearing = mainBearing;
    }
    public Double getBearingForNavigationPurpose(){
        return mBearing;
    }
}