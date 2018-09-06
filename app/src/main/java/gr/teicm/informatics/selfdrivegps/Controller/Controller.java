package gr.teicm.informatics.selfdrivegps.Controller;

import android.app.FragmentManager;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;

public class Controller {
    private static ArrayList<LatLng> fieldArrayList, lineArrayList, mainLineFocus, secondLineFocus;
    private static ArrayList<ArrayList<LatLng>> lineTest, mPassedLine;
    private static String idOfList, mLocationStatus, mStatus ="Record field selected";
    private static Integer mRange ;
    private static GoogleMap gMap;
    private static Double mBearing;
    private static FragmentManager mFragmentManager;

    public static final String MODE_1_RECORD_FIELD = "Record Field";
    public static final String MODE_2_CREATE_LINE = "Create Line";
    public static final String MODE_3_DRIVING = "Driving";
    public static final String LEFT = "left";
    public static final String RIGHT= "right";
    public static final String MID = "mid";
    public static final String NONE = "none of them";

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
    public void setArrayListForLine(ArrayList<LatLng> mainLinePoints){
        lineArrayList = mainLinePoints;
    }
    public ArrayList<LatLng> getArrayListForLine(){
        return lineArrayList;
    }
    //Setter/Getter for ArrayList<LatLng> which refer to main Line which user focus on navigation mode
    public void setArrayListForLineToFocus(ArrayList<LatLng> lineToFocus){
        mainLineFocus = lineToFocus;
    }
    public ArrayList<LatLng> getArrayListForLineToFocus(){
        return mainLineFocus;
    }
    //Setter/Getter for ArrayList<LatLng> which refer to second Line that activated and user focus on navigation mode
    public void setSecondLineThatActivated(ArrayList<LatLng> secondLineToFocus){
        secondLineFocus = secondLineToFocus;
    }
    public ArrayList<LatLng> getSecondLineThatActivated(){
        return secondLineFocus;
    }

    //Setter/Getter for ArrayList<ArrayList<LatLng>> of multiplied polyLines
    public void setArrayListOfMultipliedPolyLines(ArrayList<ArrayList<LatLng>> linePoints){
        lineTest = linePoints;
    }
    public ArrayList<ArrayList<LatLng>> getArrayListOfMultipliedPolyLines(){
        return lineTest;
    }
    //Setter/Getter for ArrayList<ArrayList<LatLng>> of multiplied polyLines
    public void setArrayListOfPassedPolyLines(ArrayList<ArrayList<LatLng>> passedLine){
        mPassedLine = passedLine;
    }
    public ArrayList<ArrayList<LatLng>> getArrayListOfPlacedPolyLines(){
        return mPassedLine;
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
    //Setter/Getter set status that corresponds on place for navigation bar
    public void setLocationOfUserForNavigationBar(String statusForLocation){
        mLocationStatus = statusForLocation;
    }
    public String getLocationOfUserForNavigationBar(){
        return mLocationStatus;
    }

    //Setter/Getter to interact with range meter of settingActivity
    public void setMeterOfRange(Integer counter){
        mRange = counter;
    }
    public Integer getMeterOfRange(){
        return mRange;
    }

    //Setter/Getter to set GoogleMap to work on DialogMainFunction
    public void setGoogleMap(GoogleMap map){
        gMap = map;
    }
    public GoogleMap getGoogleMap(){
        return gMap;
    }

    //Setter/Getter for bearing to use it for the navigationAlgorithm
    public void setBearingForNavigationPurpose(Double mainBearing){
        mBearing = mainBearing;
    }
    public Double getBearingForNavigationPurpose(){
        return mBearing;
    }

    //Setter/Getter for bearing to use it for the navigationAlgorithm
    public void setAppFragmentManager(FragmentManager fragManager){
        mFragmentManager = fragManager;
    }
    public FragmentManager getAppFragmentManager(){
        return mFragmentManager;
    }
}