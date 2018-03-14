package gr.teicm.informatics.selfdrivegps;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by Dimitriadis983 on 13-Mar-18.
 */

public class LatLngCollection extends AsyncTask<ArrayList,String, ArrayList> {

//    private ArrayList<LatLng> points; //added
    final DatabaseReference myRef1 = FirebaseDatabase.getInstance().getReference();

//    ArrayList mPoint = MapsActivity.a


    @Override
    protected ArrayList doInBackground(ArrayList[] arrayLists) {

//        points = new ArrayList();
//        myRef1.setValue(points);

        return null;
    }
}
