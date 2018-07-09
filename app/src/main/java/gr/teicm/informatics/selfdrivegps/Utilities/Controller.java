package gr.teicm.informatics.selfdrivegps.Utilities;

import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;

public class Controller {
    private static ArrayList<LatLng> arrayList;
    private static String idOfList;
    private static LatLng mLatLng;
    private static float mCounter;

    public void setPoints(ArrayList<LatLng> points){
        arrayList = points;
    } //Setter/Getter for ArrayList<LatLng>
    public ArrayList<LatLng> getPoints(){
        return arrayList;
    }

    public void setIdOfListView(String id){
        idOfList = id;
    } //Setter/Getter for get id from list of FireBase
    public String getIdOfListView(){
        return idOfList;
    }

    public void setLocationOfUser(LatLng latLng){
        mLatLng = latLng;
    } //Setter/Getter to get current LatLng of user
    public LatLng getLocationOfUser() {
        return mLatLng;
    }

    public void setMeterOfRange(float counter){
        mCounter = counter;
    } //Setter/Getter to interact with range meter of settingActivity
    public float getMeterOfRange(){
        return mCounter;
    }
}
