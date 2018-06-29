package gr.teicm.informatics.selfdrivegps.Utilities;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class Controller {
    private static ArrayList<LatLng> arrayList;
    private static String idOfList;

    //Setter for ArrayList<LatLng>
    public void setPoints(ArrayList<LatLng> points){
        arrayList = points;
    }
    //Getter for ArrayList<LatLng>
    public ArrayList<LatLng> getPoints(){
        return arrayList;
    }

    public static void setIdOfListView(String id){
        idOfList = id;
    }

    public static String getIdOfListView(){
        return idOfList;
    }
}
