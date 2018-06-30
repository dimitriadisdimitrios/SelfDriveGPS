package gr.teicm.informatics.selfdrivegps.Utilities;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class Controller {
    private static ArrayList<LatLng> arrayList;
    private static String idOfList;
    private static LatLng mLatLng;

    //Setter for ArrayList<LatLng>
    public void setPoints(ArrayList<LatLng> points){
        arrayList = points;
    }
    //Getter for ArrayList<LatLng>
    public ArrayList<LatLng> getPoints(){
        return arrayList;
    }

    public void setIdOfListView(String id){
        idOfList = id;
    }

    public String getIdOfListView(){
        return idOfList;
    }

    public void setLocationOfUser(LatLng latLng){
        mLatLng = latLng;
    }

    public LatLng getLocationOfUser() {
        return mLatLng;
    }
}
